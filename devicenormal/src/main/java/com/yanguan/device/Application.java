package com.yanguan.device;

import com.yanguan.device.handle.IdleHandler;
import com.yanguan.device.nio.NettyServer;
import com.yanguan.device.vo.DeviceStatus;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 14:35)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class Application {
    private static final Logger logger = Logger.getLogger(Application.class);
    private static Application application;
    private ApplicationContext appContext;
    private static ScheduledExecutorService schedu;
    /**
     * Get application instance
     *
     * @return
     */
    public static Application getApplication() {
        if (application == null) {
            application = new Application();
        }
        return application;
    }

    /**
     * Get application context
     *
     * @return
     */
    public ApplicationContext getAppContext() {
        return appContext;
    }
    public synchronized static ScheduledExecutorService getSchedu() {
        return schedu == null ? (schedu = Executors.newScheduledThreadPool(3)) : schedu;
    }

    /**
     * start the command service
     */
    public void start() {
        appContext = new ClassPathXmlApplicationContext(new String[]{
                "spring-application.xml"
        });

        NettyServer server = appContext.getBean(NettyServer.class);
        getSchedu().scheduleAtFixedRate(new Runnable() {
            private Runnable runnable;
            {
                runnable = this;
            }

            @Override
            public void run() {
                logger.info("offine device .....");
                long curTime = System.currentTimeMillis();
                for (int devId : IdleHandler.getConnectionMap().keySet()) {
                    DeviceStatus status = IdleHandler.getDeviceStatuss().get(devId);
                    if (status != null && curTime - status.getLastSendTime() >= server.getWriteIdle()) {
                        IdleHandler.getConnectionMap().remove(devId);
                        IdleHandler.getDeviceStatuss().remove(devId);
                    }
                }
                Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        e.printStackTrace();
                        getSchedu().scheduleAtFixedRate(runnable, server.getWriteIdle(), server.getWriteIdle(), TimeUnit.SECONDS);
                    }
                });
            }
        }, server.getWriteIdle(), server.getWriteIdle(), TimeUnit.SECONDS);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.start();
    }

    /**
     * destroy the command service
     *
     */
    public void destroy() {
        NettyServer server = appContext.getBean(NettyServer.class);
        server.shutdown();
        System.exit(0);
    }

    public static void main(String[] args) {
        Application.getApplication().start();
    }
}
