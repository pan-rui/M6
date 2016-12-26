package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import com.yanguan.device.mq.AppPush;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("Status_Update")
public class StatusUpdate implements IProcess {
    private static final Logger logger = Logger.getLogger(StatusUpdate.class);
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private AppPush appPush;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId = (int) data.get("devId");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("Device_Mobility_Status", data.get("sport"));
        params.put("Device_Battery_Residual", data.get("voltage"));
        params.put("Device_Defence_Status", data.get("defence"));
        params.put("Device_Ptl_Elec_Status", data.get("power"));
        params.put("Device_Broke", data.get("brake"));
        params.put("Device_ID", devId);
        try {
            deviceMapper.updateByProsInTab(params, Constant.DEVICE_TABLE_REF);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Device Status Update is Fail.the DeviceID:"+devId);
        }
        appPush.sendMessage(devId, Constant.Push_Status, System.currentTimeMillis(),null);
    }
}
