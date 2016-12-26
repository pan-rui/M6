package com.yanguan.device.cmd;

import com.yanguan.device.model.Constant;
import com.yanguan.device.task.GpsWriteDB;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
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
@Component("Real_Update4")
public class RealUpdate4 implements IProcess {
    private static final Logger logger = Logger.getLogger(RealUpdate4.class);
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
        String key=Constant.Device_Real_Prefix+devId;
        Jedis jedis=Constant.jedisPool.getResource();
        jedis.rpush(key, lon1.toString()+lat1+time1,lon2.toString()+lat2+time2,lon3.toString()+lat3+time3,lon4.toString()+lat4+time4);
        if (jedis.llen(key) >= 15) {
            jedis.del(key);
        }
        jedis.close();
        synchronized (GpsWriteDB.gpsList) {
            GpsWriteDB.gpsList.add(new Object[]{devId,lon1,lat1,time1});
            GpsWriteDB.gpsList.add(new Object[]{devId,lon2,lat2,time2});
            GpsWriteDB.gpsList.add(new Object[]{devId,lon3,lat3,time3});
            GpsWriteDB.gpsList.add(new Object[]{devId,lon4,lat4,time4});
        }
//            channel.writeAndFlush(data.get("iType")+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success);
    }
}
