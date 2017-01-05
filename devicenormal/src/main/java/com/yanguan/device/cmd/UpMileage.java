package com.yanguan.device.cmd;

import com.yanguan.device.dao.DevNormalMapper;
import com.yanguan.device.handle.IdleHandler;
import com.yanguan.device.model.Constant;
import com.yanguan.device.mq.AppPush;
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
@Component("Up_Mileage")
public class UpMileage implements IProcess {
    private static final Logger logger = Logger.getLogger(UpMileage.class);
    @Autowired
    private DevNormalMapper devNormalMapper;
    @Autowired
    private AppPush appPush;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        logger.info("up Mileage.....in the DeviceID:"+devId);
        float mileage = (float)data.get("mileage");
        appPush.sendMessage(devId,Constant.Push_Device_Mileage,System.currentTimeMillis(),mileage);
        int resultCode=Constant.Push_Cmd_Success;
        try {
            devNormalMapper.insertOrUpdateMileage(devId, mileage);
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = Constant.Push_Cmd_Fail;
            logger.error("device up Mileage is error.DeviceID:"+devId);
        }
        channel.writeAndFlush(data.get("iType")+Constant.SPLIT_CHAR+resultCode+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success);
    }
}
