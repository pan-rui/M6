package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import com.yanguan.device.mq.AppPush;
import com.yanguan.device.service.CommonService;
import com.yanguan.device.task.CmdWriteDB;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("App_Cmd1")
public class AppCmd1 implements IProcess {
    private static final Logger logger = Logger.getLogger(AppCmd1.class);
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private AppPush appPush;

    @Override
    public void process(Channel channel, Map<String, Object> data) {
        int devId = (int) data.get("devId");
        int cmdType = (int) data.get("cmdType");
        int resultCode = (int) data.remove("resultCode");
        Calendar calendar = Calendar.getInstance();
        appPush.sendMessage(devId, resultCode, calendar.getTimeInMillis(), null);
        Object val = data.get("p1");
        if (resultCode == 0) {
            Map<String, Object> params = new LinkedHashMap<>();
            switch (cmdType) {
                case 1:     //解设防
                    params.put("Device_Defence_Status", val);
                    params.put("Device_Defence_Upd_Time", calendar.getTime());
                    break;
                case 2:     //启动电源
                    params.put("Device_Ptl_Elec_Status", val);
                    break;
                case 3:     //刹车
                    params.put("Device_Broke", val);
                    params.put("Device_Broke_Upd_Time", calendar.getTime());
                    break;
/*                case 4:     //实时跟踪
                    break;*/
                case 5:     //滚动告警设置
                    params.put("Device_Roll_Rmd", val);
                    params.put("Device_Roll_Rmd_Upd_Time", calendar.getTime());
                    break;
                case 6:     //震动告警
                    params.put("Device_Shake_Remind", val);
                    params.put("Device_Shake_Rmd_Upd_Time", calendar.getTime());
                    break;
                case 7:     //控车模式
                    params.put("Device_Ctrl_Model", val);
                    params.put("Device_Ctrl_Model_Upd_Time", calendar.getTime());
                    break;
                case 8:     //灵敏度
                    params.put("Device_Sensibility", val);
                    params.put("Device_Sensibility_Upd_Time", calendar.getTime());
                    break;
                case 9:     //喇叭开关
                    params.put("Device_Rmd_Voice", val);
                    params.put("Device_Rmd_Voice_Upd_Time", calendar.getTime());
                    break;
/*                case 10:        //获取状态
                    params.put("Device_ID", devId);
                    List<Map<String,Object>> resultList=deviceMapper.queryByProsInTab(params, Constant.DEVICE_TABLE_REF);
                    if(!resultList.isEmpty()){

                        channel.writeAndFlush();
                    }
                    break;
                case 11:        //设备上传速度设置
                    break;*/
/*                case 12:
                    break;
                case 13:
                    break;*/
            }
            params.put("Device_ID", devId);
            try {
                deviceMapper.updateByProsInTab(params, Constant.DEVICE_TABLE_REF);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("update Status Fail..the DeviceID:" + devId);
            }
        }
        String cmdStr=resultCode+generateStatus(data);
        synchronized (CmdWriteDB.cmdList) {
            CmdWriteDB.cmdList.add(cmdStr);
        }
    }

    public static String generateStatus(Map<String, Object> map) {
        StringBuffer sb = new StringBuffer();
/*        sb.append(map.get("iType")).append(Constant.SPLIT_CHAR)
                .append(map.get("pName")).append(Constant.SPLIT_CHAR)
                .append(map.get("sVer")).append(Constant.SPLIT_CHAR)
                .append(map.get("devId")).append(Constant.SPLIT_CHAR)
                .append(map.get("cmdType")).append()*/
//        Set<Map.Entry<String,Object>> entrySet = map.entrySet();
//        entrySet.remove(entrySet.toArray()[entrySet.size() - 1]);
        map.forEach((k, v) -> sb.append(Constant.SPLIT_CHAR).append(v));
        return sb.toString();
    }
}
