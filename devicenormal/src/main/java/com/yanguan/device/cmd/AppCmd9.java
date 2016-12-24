package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import com.yanguan.device.service.CommonService;
import com.yanguan.device.task.CmdWriteDB;
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
@Component("App_Cmd9")
public class AppCmd9 implements IProcess {
    private static final Logger logger = Logger.getLogger(AppCmd9.class);
    @Autowired
    private DeviceMapper deviceMapper;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId = (int) data.get("devId");
        int resultCode = (int) data.get("resultCode");
        Calendar calendar = Calendar.getInstance();
        if (resultCode == 0) {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("Device_Elec_Defence_Value", data.get("p1"));
            params.put("Device_Elec_Defence_Lon", data.get("lon"));
            params.put("Device_Elec_Defence_Lat", data.get("lat"));
            params.put("Device_ID", devId);
            try {
                deviceMapper.updateByProsInTab(params, Constant.DEVICE_TABLE_REF);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Elec_Defence is set Fail.the DeviceID:"+devId);
            }
        }
        synchronized (CmdWriteDB.cmdList) {
            CmdWriteDB.cmdList.add(resultCode + Constant.SPLIT_CHAR);
        }
    }
}
