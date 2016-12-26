package com.yanguan.device.cmd;

import com.yanguan.device.model.Constant;
import com.yanguan.device.mq.AppPush;
import com.yanguan.device.task.RmdWriteDB;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("Alert")
public class Alert implements IProcess {
    private static final Logger logger = Logger.getLogger(Alert.class);
    @Autowired
    private AppPush appPush;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        Calendar calendar=Calendar.getInstance();
        appPush.sendMessage(devId, (int) data.get("alertType"), calendar.getTimeInMillis(), null);
        synchronized (RmdWriteDB.rmdList) {
            RmdWriteDB.rmdList.add(new Object[]{devId,data.get("alertType"),data.get("lon"),data.get("lat"),data.get("time")});
        }
        channel.writeAndFlush(data.get("iType")+Constant.SPLIT_CHAR+data.get("pName")+Constant.SPLIT_CHAR+data.get("sVer")+Constant.SPLIT_CHAR+devId+Constant.SPLIT_CHAR+data.get("alertType")+Constant.SPLIT_CHAR+Constant.Push_Cmd_Success+Constant.SPLIT_CHAR+0);
    }
}
