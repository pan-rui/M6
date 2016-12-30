package com.yanguan.device.cmd;

import com.yanguan.device.model.Constant;
import com.yanguan.device.task.GpsWriteDB;
import io.netty.channel.Channel;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.net.SocketAddress;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("Track1")
public class Track1 implements IProcess {
    private static final Logger logger = Logger.getLogger(Track1.class);
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        Object lon1 = data.get("lon1");
        Object lat1 = data.get("lat1");
        Object time1 = data.get("time1");
        Object[] gps1=new Object[]{devId,lon1,lat1,time1};
        synchronized (GpsWriteDB.gpsList) {
            GpsWriteDB.gpsList.add(gps1);
        }
             channel.writeAndFlush(new DefaultAddressedEnvelope<String,SocketAddress>(data.get("iType")+Constant.SPLIT_CHAR+devId+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success+Constant.SPLIT_CHAR+0,(SocketAddress)data.get("sender"),(SocketAddress)data.get("recipient")));
    }
}
