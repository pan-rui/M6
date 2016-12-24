package com.yanguan.device.handle;

import com.yanguan.device.cmd.IProcess;
import com.yanguan.device.model.ProtocolEnum;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

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
public class ScheduServerHandle extends SimpleChannelInboundHandler<Map<String,Object>> implements ApplicationContextAware {
    private ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext=applicationContext;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Map<String,Object> map) throws Exception {
        short iType = (short) map.get("iType");
        map.forEach((k,v)-> System.out.println("k-->"+k+"\tv--->"+v));      //Test......
        IProcess service= (IProcess) appContext.getBean(ProtocolEnum.valueOfType(iType).name());
        service.process(channelHandlerContext.channel(),map);
    }
}
