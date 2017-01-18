package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import com.yanguan.device.service.CommonService;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
@Component("Active_Card")
public class ActiveCard implements IProcess {
    private static final Logger logger = Logger.getLogger(ActiveCard.class);
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private CommonService commonService;

    @Override
    public void process(Channel channel, Map<String, Object> data) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("Device_ID", data.get("devId"));
        StringBuffer sb = new StringBuffer(data.get("iType") + Constant.SPLIT_CHAR);
        sb.append(data.get("pName")).append(Constant.SPLIT_CHAR)
                .append(data.get("sVer")).append(Constant.SPLIT_CHAR)
                .append(data.get("devId")).append(Constant.SPLIT_CHAR);
        int exeResult = 0;
        if (!StringUtils.isEmpty(data.get("iccid"))) {
            commonService.activeCard(data.get("iccid"), data.get("devId"));
        }else exeResult=1;
        sb.append(exeResult).append(Constant.SPLIT_CHAR).append(65535);
        channel.writeAndFlush(sb.toString());
    }
}
