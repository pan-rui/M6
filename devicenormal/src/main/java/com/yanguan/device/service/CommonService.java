package com.yanguan.device.service;

import com.yanguan.device.dao.DevNormalMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-19 17:18)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Service
public class CommonService {
    private static final Logger logger = Logger.getLogger(CommonService.class);
    @Autowired
    private DevNormalMapper devNormalMapper;
    public int activeCard(Object iccid,Object deviceID) {
        int updateResult = 0;
        Map<String, Object> paramss = new LinkedHashMap<>();
        paramss.put("Active_Time", new Date());
        try {
            updateResult= devNormalMapper.cardActive(String.valueOf(iccid));
        } catch (Exception e) {
            logger.error("save Device ActiveTime is error! on the DeviceID:" + deviceID);
        }
        if(updateResult<1)
            logger.warn("valid: sim Card already bind.The iccid:"+iccid+"\ton the requested DeviceID:"+deviceID);
        return updateResult;
    }

    public boolean isNotify() {
        return true;
    }

}
