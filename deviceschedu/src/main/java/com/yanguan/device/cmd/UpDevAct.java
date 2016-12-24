package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("UP_DevAct")
public class UpDevAct implements IProcess {
    private static final Logger logger = Logger.getLogger(UpDevAct.class);
    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public void process(Channel channel, Map<String, Object> data) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("Software_Version", data.get("softVer"));
        params.put("ICCID", data.get("iccid"));
        params.put("Device_Active_Time", new Date());
        params.put("Device_Imei", data.get("imei"));
        int result = 0;
        try {
            deviceMapper.updateByProsInTab(params, Constant.DEVICE_TABLE_INFO);
        } catch (Exception e) {
            logger.error("up activeTime is error..on the Device_IMEI:" + data.get("imei"));
            result = 0;
        }
        StringBuffer sb = new StringBuffer(data.get("iType") + Constant.SPLIT_CHAR);
        sb.append(data.get("imei")).append(Constant.SPLIT_CHAR)
                .append(data.get("softVer")).append(Constant.SPLIT_CHAR)
                .append(data.get("iccid")).append(Constant.SPLIT_CHAR)
                .append(result).append(Constant.SPLIT_CHAR)
                .append(65535);
        channel.writeAndFlush(sb.toString());
    }
}
