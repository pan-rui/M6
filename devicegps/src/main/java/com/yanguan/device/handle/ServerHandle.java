package com.yanguan.device.handle;

import com.yanguan.device.cmd.IProcess;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private ThreadPoolExecutor tpe;
    @Value("#{config['nio.coreSize']?:256}")
    private int coreSize;
    @Value("#{config['nio.maxSize']?:4096}")
    private int maxSize;
    @Value("#{config['nio.keepAlive']?:15000}")
    private long keepAlive;
    protected static Map<Integer, IProcess> serviceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        serviceMap.put(41, (IProcess) appContext.getBean("Track1"));
        serviceMap.put(42, (IProcess) appContext.getBean("Track2"));
        serviceMap.put(43, (IProcess) appContext.getBean("Track3"));
        serviceMap.put(44, (IProcess) appContext.getBean("Track4"));
        serviceMap.put(45, (IProcess) appContext.getBean("Track5"));
        tpe=new ThreadPoolExecutor(coreSize,maxSize,keepAlive, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(maxSize));
        tpe.allowCoreThreadTimeOut(true);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Map<String, Object> map) throws Exception {
        int iType = (short) map.get("iType");
//        map.forEach((k, v) -> System.out.println("k-->" + k + "\tv--->" + v));      //Test......
        tpe.submit(() -> serviceMap.get(iType).process(channelHandlerContext.channel(), map));
    }
}
