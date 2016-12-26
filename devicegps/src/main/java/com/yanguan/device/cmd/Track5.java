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
        synchronized (GpsWriteDB.gpsList) {
            GpsWriteDB.gpsList.add(new Object[]{devId,lon1,lat1,time1});
            GpsWriteDB.gpsList.add(new Object[]{devId,lon2,lat2,time2});
            GpsWriteDB.gpsList.add(new Object[]{devId,lon3,lat3,time3});
            GpsWriteDB.gpsList.add(new Object[]{devId,lon4,lat4,time4});
            GpsWriteDB.gpsList.add(new Object[]{devId,lon5,lat5,time5});
        }
        channel.writeAndFlush(data.get("iType")+Constant.SPLIT_CHAR+devId+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success);
    }
}
