
package com.yanguan.device.nio;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import com.yanguan.device.mq.AppPush;
import com.yanguan.device.vo.DeviceStatus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

/**
 * @author panrui
 * @datetime 2016-12-19
 * @since version 1.0
 */
@Component
@ChannelHandler.Sharable
public class IdleHandler extends ChannelDuplexHandler {
    private static final Logger logger = Logger.getLogger(IdleHandler.class);
    protected static ConcurrentHashMap<Integer,DeviceStatus> deviceStatuss=new ConcurrentHashMap<Integer,DeviceStatus>();
    protected static ConcurrentHashMap<Integer,Channel> connectionMap=new ConcurrentHashMap<Integer,Channel>();
    public static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
            "HEARTBEAT", CharsetUtil.UTF_8));
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private AppPush appPush;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        super.channelInactive(ctx);
    }

    public static ConcurrentHashMap<Integer, DeviceStatus> getDeviceStatuss() {
        return deviceStatuss;
    }

    public static ConcurrentHashMap<Integer, Channel> getConnectionMap() {
        return connectionMap;
    }

    //存在返回true
    public boolean removeDevice(Channel channel) {
        Set<Map.Entry<Integer,Channel>> entry=connectionMap.entrySet();
        Optional<Map.Entry<Integer,Channel>> optional=StreamSupport.stream(entry.spliterator(),true).filter(en->{return en.getValue()==channel?true:false;}).findFirst();
        if(optional.isPresent()){
            Map.Entry<Integer,Channel> ent = optional.get();
            entry.remove(ent);
            Jedis jedis=Constant.jedisPool.getResource();
            jedis.hdel(Constant.Device_Is_OnLine, String.valueOf(ent.getKey()));
            jedis.close();
            Calendar calendar=Calendar.getInstance();
            //入队推送
            appPush.sendMessage(ent.getKey(),Constant.Push_OffineLine,calendar.getTimeInMillis());
            //写库
            try {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("Device_Offline_Time",calendar.getTime() );
                params.put("Device_ID",ent.getKey());
                deviceMapper.updateByProsInTab(params, Constant.DEVICE_TABLE_REF);
            } catch (Exception e) {
                logger.error("Device is Offine ,but update "+Constant.DEVICE_TABLE_REF+"  the FAIL.DeviceID:"+ent.getKey());
            }
            return true;
        }
        return false;
    }

    //存在返回true
    public boolean putDevice(int devId, Channel channel) {
        Channel channel1=connectionMap.putIfAbsent(devId, channel);
        if(channel1==null){
            Calendar calendar=Calendar.getInstance();
            appPush.sendMessage(devId,Constant.Push_OnLine,calendar.getTimeInMillis());
            try {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("Dev_Login_Time",calendar.getTime() );
                params.put("Device_ID",devId);
                deviceMapper.updateByProsInTab(params,Constant.DEVICE_TABLE_REF);
            } catch (Exception e) {
                logger.error("Device is onLine,but update "+Constant.DEVICE_TABLE_REF+"  the FAIL.DeviceID:"+devId);
            }
            return false;
        }
        return true;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            if (stateEvent.state() == IdleState.READER_IDLE) {
                logger.error("删除设备\t" + removeDevice(ctx.channel()));
                ctx.close();
//                ctx.writeAndFlush(HEARTBEAT_SEQUENCE)
//                            .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else if (stateEvent.state() == IdleState.WRITER_IDLE) {
/*                logger.info("idle...........");
                for (int devId : NioClientManager.getClients().keySet()) {
                    DeviceConnection connection = NioClientManager.getClients().get(devId);
                    if (connection.channel() == ctx.channel()) {
                        if (System.currentTimeMillis() - connection.lastSendPktTime().getTime() >= writeIdleTime)
                            clientManager.removeClient(connection.channel());
                        break;
                    }
                }*/
            } else if (stateEvent.state() == IdleState.ALL_IDLE) {}
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
