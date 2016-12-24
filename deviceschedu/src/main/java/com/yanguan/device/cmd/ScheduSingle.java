package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component("SCHEDU_SINGLE")
public class ScheduSingle implements IProcess {

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public void process(Channel channel, Map<String, Object> data) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("companyCode", String.valueOf(data.get("company")));
        List<Map<String, Object>> resultList = deviceMapper.queryByProsInTab(params, "YG_DEVICE_SCHEDU");
        if (resultList.isEmpty()) return;
        Map<String, Object> map = resultList.get(0);
        StringBuffer sb = new StringBuffer(data.get("iType") + Constant.SPLIT_CHAR + data.get("company") + Constant.SPLIT_CHAR);
        Object sType=data.get("sType");
        switch ((int)sType ) {
            case 0:
                sb.append(map.get("agpsIP")).append(Constant.SPLIT_CHAR).append(map.get("agpsPort")).append(Constant.SPLIT_CHAR);
                break;
            case 1:
                sb.append(map.get("realIP")).append(Constant.SPLIT_CHAR).append(map.get("realPort")).append(Constant.SPLIT_CHAR);
                break;
            case 2:
                sb.append(map.get("trackIP")).append(Constant.SPLIT_CHAR).append(map.get("trackPort")).append(Constant.SPLIT_CHAR);
                break;
            case 3:
                sb.append(map.get("cmdIP")).append(Constant.SPLIT_CHAR).append(map.get("cmdPort")).append(Constant.SPLIT_CHAR);
                break;
            case 4:
                sb.append(map.get("phoneIP")).append(Constant.SPLIT_CHAR).append(map.get("phonePort")).append(Constant.SPLIT_CHAR);
                break;
            case 5:
                sb.append(map.get("backupIP")).append(Constant.SPLIT_CHAR).append(map.get("backupPort")).append(Constant.SPLIT_CHAR);
                break;
        }
        sb.append(sType).append(Constant.SPLIT_CHAR);
        sb.append(65535);
        channel.writeAndFlush(sb.toString());
    }
}
