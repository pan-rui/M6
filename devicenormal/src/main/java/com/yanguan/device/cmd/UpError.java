package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
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
@Component("Up_Error")
public class UpError implements IProcess {
    private static final Logger logger = Logger.getLogger(UpError.class);
    @Autowired
    private DeviceMapper deviceMapper;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId= (int)data.get("devId");
        int errorCode= (int) data.get("errorCode");
        logger.info("up Error .....in the DeviceID:"+devId+"\t errorCode:"+errorCode);
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("devId", devId);
        errorMap.put("projectName", data.get("pName"));
        errorMap.put("version", data.get("sVer"));
        errorMap.put("errorCode", errorCode);
        errorMap.put("time", new Date());
        try {
            deviceMapper.insertByProsInTab(errorMap);
        } catch (Exception e) {
            logger.error("save ErrorCode is FAIL. the DeviceID:"+devId+"\t errorCode:"+errorCode);
        }
    }
}
