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
@Component("Real_Update1")
public class RealUpdate1 implements IProcess {
    private static final Logger logger = Logger.getLogger(RealUpdate1.class);
    @Autowired
    private AppPush appPush;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        Object lon1 = data.get("lon1");
        Object lat1 = data.get("lat1");
        Object time1 = data.get("time1");
        String key=Constant.Device_Real_Prefix+devId;
/*        Jedis jedis=Constant.jedisPool.getResource();
        jedis.rpush(key, lon1.toString()+Constant.SPLIT_CHAR+lat1+Constant.SPLIT_CHAR+time1);
        if (jedis.llen(key) >= 15) {
            jedis.del(key);
        }
        jedis.close();*/
        appPush.sendMessage(devId,Constant.Push_Device_Real_Track,((int)time1)*1000l,lon1.toString()+Constant.SPLIT_CHAR+lat1+Constant.SPLIT_CHAR+time1);
        Object[] objArry=new Object[]{devId,lon1,lat1,time1};
        synchronized (GpsWriteDB.gpsList) {
            GpsWriteDB.gpsList.add(objArry);
        }
//            channel.writeAndFlush(data.get("iType")+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success);
    }
}
