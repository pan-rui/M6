package com.yanguan.device.cmd;

import com.yanguan.device.dao.DevNormalMapper;
import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import com.yanguan.device.service.CommonService;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("Take_DevID")
public class TakeDevID implements IProcess {
    private static final Logger logger = Logger.getLogger(TakeDevID.class);
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private CommonService commonService;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("Device_Imei", data.get("imei"));
        List<Map<String, Object>> resultList = deviceMapper.queryByProsInTab(params, Constant.DEVICE_TABLE_INFO);
        Map<String, Object> deviceInfo = null;
        if (resultList.isEmpty()) {
            return;
        } else deviceInfo = resultList.get(0);
        StringBuffer sb = new StringBuffer(data.get("iType") + Constant.SPLIT_CHAR);
        sb.append(data.get("pName")).append(Constant.SPLIT_CHAR)
                .append(data.get("sVer")).append(Constant.SPLIT_CHAR)
                .append(deviceInfo.get("Device_ID")).append(Constant.SPLIT_CHAR)
                .append(65535);
        channel.writeAndFlush(sb.toString());
        commonService.activeCard(data.get("iccid"),deviceInfo.get("Device_ID"));
    }
}
