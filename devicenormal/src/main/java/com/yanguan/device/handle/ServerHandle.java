package com.yanguan.device.handle;

import com.yanguan.device.cmd.IProcess;
import com.yanguan.device.model.ProtocolEnum;
import com.yanguan.device.vo.DeviceStatus;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 15:08)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Service
@ChannelHandler.Sharable
public class ServerHandle extends SimpleChannelInboundHandler<Map<String, Object>> implements ApplicationContextAware {
    private ApplicationContext appContext;
    protected static Map<Integer, IProcess> serviceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        serviceMap.put(7, (IProcess) appContext.getBean("Take_DevID"));
        serviceMap.put(9, (IProcess) appContext.getBean("Active_Card"));
        serviceMap.put(20, (IProcess) appContext.getBean("App_Cmd0"));
        serviceMap.put(21, (IProcess) appContext.getBean("App_Cmd1"));
        serviceMap.put(29, (IProcess) appContext.getBean("App_Cmd9"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Map<String, Object> map) throws Exception {
        short iType = (short) map.get("iType");
        map.forEach((k, v) -> System.out.println("k-->" + k + "\tv--->" + v));      //Test......
        Object devId = map.get("devId");
        if (iType != 16 && devId != null) {
            DeviceStatus deviceStatus = IdleHandler.getDeviceStatuss().get(devId);
            if (deviceStatus != null)
                deviceStatus.setLastSendTime(System.currentTimeMillis());
        }
        IProcess service = (IProcess) appContext.getBean(ProtocolEnum.valueOfType(iType).name());
        service.process(channelHandlerContext.channel(), map);
    }
}
