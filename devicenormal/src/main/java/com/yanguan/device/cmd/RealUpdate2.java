package com.yanguan.device.cmd;

import com.yanguan.device.model.Constant;
import com.yanguan.device.mq.AppPush;
import com.yanguan.device.task.GpsWriteDB;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("Real_Update2")
public class RealUpdate2 implements IProcess {
    private static final Logger logger = Logger.getLogger(RealUpdate2.class);
    @Autowired
    private AppPush appPush;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        Object lon1 = data.get("lon1");
        Object lat1 = data.get("lat1");
        Object time1 = data.get("time1");
        Object lon2 = data.get("lon2");
        Object lat2 = data.get("lat2");
        Object time2 = data.get("time2");
        String key=Constant.Device_Real_Prefix+devId;
/*        Jedis jedis=Constant.jedisPool.getResource();
        jedis.rpush(key, lon1.toString()+Constant.SPLIT_CHAR+lat1+Constant.SPLIT_CHAR+time1,lon2.toString()+Constant.SPLIT_CHAR+lat2+Constant.SPLIT_CHAR+time2);
        if (jedis.llen(key) >= 12) {
            jedis.del(key);
        }
        jedis.close();*/
        appPush.sendMessage(devId,Constant.Push_Device_Real_Track,((int)time1)*1000l,lon1.toString()+Constant.SPLIT_CHAR+lat1+Constant.SPLIT_CHAR+time1+Constant.JOIN_CHAR+lon2.toString()+Constant.SPLIT_CHAR+lat2+Constant.SPLIT_CHAR+time2);
        Object[] objArry1=new Object[]{devId,lon1,lat1,time1};
        Object[] objArry2=new Object[]{devId,lon2,lat2,time2};
        synchronized (GpsWriteDB.gpsList) {
            GpsWriteDB.gpsList.add(objArry1);
            GpsWriteDB.gpsList.add(objArry2);
        }
//            channel.writeAndFlush(data.get("iType")+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success);
    }
}
