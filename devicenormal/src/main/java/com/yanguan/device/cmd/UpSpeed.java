package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import com.yanguan.device.mq.AppPush;
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
@Component("Up_Speed")
public class UpSpeed implements IProcess {
    private static final Logger logger = Logger.getLogger(UpSpeed.class);
    @Autowired
    private AppPush appPush;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId = (int) data.get("devId");
        Calendar calendar = Calendar.getInstance();
        appPush.sendMessage(devId, Constant.Push_Device_Speed, calendar.getTimeInMillis(),data.get("speed"));
    }
}
