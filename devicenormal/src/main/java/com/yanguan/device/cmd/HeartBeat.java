package com.yanguan.device.cmd;

import com.yanguan.device.model.Constant;
import com.yanguan.device.handle.IdleHandler;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("HeartBeat")
public class HeartBeat implements IProcess {
    private static final Logger logger = Logger.getLogger(HeartBeat.class);
    @Autowired
    private IdleHandler idleHandler;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        logger.info("heartBeat.....in the DeviceID:"+devId);
        String gsmSignal = String.valueOf(data.get("gsmSignal"));
        Jedis jedis=Constant.jedisPool.getResource();
        jedis.hset(Constant.HeartBeat,String.valueOf(data.get("devId")), gsmSignal);
        jedis.close();
        idleHandler.putDevice(devId, channel);
        channel.writeAndFlush(data.get("iType")+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success);
    }
}
