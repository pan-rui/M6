package com.yanguan.device.cmd;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.model.Constant;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component("SCHEDU")
public class Schedu implements IProcess {

    @Autowired
    private DeviceMapper deviceMapper;
    @Override
    public void process(Channel channel, Map<String, Object> data) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("companyCode", String.valueOf(data.get("company")));
        List<Map<String,Object>> resultList=deviceMapper.queryByProsInTab(params,"YG_DEVICE_SCHEDU");
        if(resultList.isEmpty()) return;
//        Map<String, Object> map = resultList.get(0);
        StringBuffer sb=new StringBuffer(data.get("iType") + Constant.SPLIT_CHAR + data.get("company") + Constant.SPLIT_CHAR);
        StreamSupport.stream(resultList.spliterator(), true).filter(map -> {
            return (int)map.get("agpsStatus")==1;
        }).limit(1).forEach(m->{sb.append(m.get("agpsProtocol")).append(Constant.SPLIT_CHAR)
                .append(m.get("agpsIP")).append(Constant.SPLIT_CHAR)
                .append(m.get("agpsPort")).append(Constant.SPLIT_CHAR);});
        StreamSupport.stream(resultList.spliterator(), true).filter(map -> {
            return (int)map.get("realStatus")==1;
        }).limit(1).forEach(m->{sb.append(m.get("realProtocol")).append(Constant.SPLIT_CHAR)
                .append(m.get("realIP")).append(Constant.SPLIT_CHAR)
                .append(m.get("realPort")).append(Constant.SPLIT_CHAR);});
        StreamSupport.stream(resultList.spliterator(), true).filter(map -> {
            return (int)map.get("trackStatus")==1;
        }).limit(1).forEach(m->{sb.append(m.get("trackProtocol")).append(Constant.SPLIT_CHAR)
                .append(m.get("trackIP")).append(Constant.SPLIT_CHAR)
                .append(m.get("trackPort")).append(Constant.SPLIT_CHAR);});
        StreamSupport.stream(resultList.spliterator(), true).filter(map -> {
            return (int)map.get("cmdStatus")==1;
        }).limit(1).forEach(m->{sb.append(m.get("cmdProtocol")).append(Constant.SPLIT_CHAR)
                .append(m.get("cmdIP")).append(Constant.SPLIT_CHAR)
                .append(m.get("cmdPort")).append(Constant.SPLIT_CHAR);});
        StreamSupport.stream(resultList.spliterator(), true).filter(map -> {
            return (int)map.get("phoneStatus")==1;
        }).limit(1).forEach(m->{sb.append(m.get("phoneProtocol")).append(Constant.SPLIT_CHAR)
                .append(m.get("phoneIP")).append(Constant.SPLIT_CHAR)
                .append(m.get("phonePort")).append(Constant.SPLIT_CHAR);});
        StreamSupport.stream(resultList.spliterator(), true).filter(map -> {
            return (int)map.get("backupStatus")==1;
        }).limit(1).forEach(m->{sb.append(m.get("backupProtocol")).append(Constant.SPLIT_CHAR)
                .append(m.get("backupIP")).append(Constant.SPLIT_CHAR)
                .append(m.get("backupPort")).append(Constant.SPLIT_CHAR);});
        sb.append(65535);
        channel.writeAndFlush(sb.toString());
    }
}
