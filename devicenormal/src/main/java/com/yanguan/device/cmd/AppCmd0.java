package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import com.yanguan.device.mq.AppPush;
import com.yanguan.device.nio.IdleHandler;
import com.yanguan.device.service.CommonService;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("App_Cmd0")
public class AppCmd0 implements IProcess {
    private static final Logger logger = Logger.getLogger(AppCmd0.class);
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private AppPush appPush;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId = (int) data.get("devId");
        int cmdType = (int) data.get("cmdType");
        int resultCode = (int) data.get("resultCode");
        Calendar calendar = Calendar.getInstance();
        appPush.sendMessage(devId, resultCode, calendar.getTimeInMillis());
    }
}
