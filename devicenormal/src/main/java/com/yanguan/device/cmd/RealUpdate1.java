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
@Component("Real_Update1")
public class RealUpdate1 implements IProcess {
    private static final Logger logger = Logger.getLogger(RealUpdate1.class);
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        Object lon1 = data.get("lon1");
        Object lat1 = data.get("lat1");
        Object time1 = data.get("time1");
        String key=Constant.Device_Real_Prefix+devId;
        Jedis jedis=Constant.jedisPool.getResource();
        jedis.rpush(key, lon1.toString()+lat1+time1);
        if (jedis.llen(key) >= 12) {
            jedis.del(key);
        }
        jedis.close();
        synchronized (GpsWriteDB.gpsList) {
            GpsWriteDB.gpsList.add(new Object[]{devId,lon1,lat1,time1});
        }
//            channel.writeAndFlush(data.get("iType")+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success);
    }
}
