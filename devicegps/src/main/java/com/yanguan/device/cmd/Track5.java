package com.yanguan.device.cmd;

import com.yanguan.device.model.Constant;
import com.yanguan.device.task.GpsWriteDB;
import io.netty.channel.Channel;
import io.netty.channel.DefaultAddressedEnvelope;
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
@Component("Track5")
public class Track5 implements IProcess {
    private static final Logger logger = Logger.getLogger(Track5.class);
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        Object lon1 = data.get("lon1");
        Object lat1 = data.get("lat1");
        Object time1 = data.get("time1");
        Object lon2 = data.get("lon2");
        Object lat2 = data.get("lat2");
        Object time2 = data.get("time2");
        Object lon3 = data.get("lon3");
        Object lat3 = data.get("lat3");
        Object time3 = data.get("time3");
        Object lon4 = data.get("lon4");
        Object lat4 = data.get("lat4");
        Object time4 = data.get("time4");
        Object lon5 = data.get("lon5");
        Object lat5 = data.get("lat5");
        Object time5 = data.get("time5");
        Object[] gps1=new Object[]{devId,lon1,lat1,time1};
        Object[] gps2=new Object[]{devId,lon2,lat2,time2};
        Object[] gps3=new Object[]{devId,lon3,lat3,time3};
        Object[] gps4=new Object[]{devId,lon4,lat4,time4};
        Object[] gps5=new Object[]{devId,lon5,lat5,time5};
        synchronized (GpsWriteDB.gpsList) {
            GpsWriteDB.gpsList.add(gps1);
            GpsWriteDB.gpsList.add(gps2);
            GpsWriteDB.gpsList.add(gps3);
            GpsWriteDB.gpsList.add(gps4);
            GpsWriteDB.gpsList.add(gps5);
        }
        channel.writeAndFlush(new DefaultAddressedEnvelope<String,SocketAddress>(data.get("iType")+Constant.SPLIT_CHAR+devId+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success+Constant.SPLIT_CHAR+0,(SocketAddress)data.get("sender"),(SocketAddress)data.get("recipient")));
    }
}
