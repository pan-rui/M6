package com.yanguan.device;

import com.yanguan.device.nio.NettyServer;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.TimeZone;

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

    /**
     * start the command service
     */
    public void start() {
        appContext = new ClassPathXmlApplicationContext(new String[]{
                "spring-application.xml"
        });

        NettyServer server = appContext.getBean(NettyServer.class);
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
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Application.getApplication().start();
    }
}
