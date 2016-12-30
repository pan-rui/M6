package com.yanguan.device.task;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by panrui on 2016/5/30.
 */
@Component
public class GpsWriteDB implements InitializingBean {
    private static final Logger logger = Logger.getLogger(GpsWriteDB.class);
    private static ScheduledExecutorService timer;
    private static Runnable timerTask;
    @Value("#{config['gps.write.interval']}")
    private int interval;
    @Value("#{config['jdbc.username']}")
    private String userName;
    @Value("#{config['jdbc.password']}")
    private String password;
    @Value("#{config['jdbc.url']}")
    private String url;
    @Value("#{config['jdbc.url1']}")
    private String url1;
    @Value("#{config['jdbc.url2']}")
    private String url2;
    private String[] urls = null;
    private String mysqlDriver = "com.mysql.cj.jdbc.Driver";
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    public static final LinkedList<Object[]> gpsList = new LinkedList<>();

    public synchronized static ScheduledExecutorService getTimer() {
        return timer == null ? (timer = Executors.newSingleThreadScheduledExecutor()) : timer;
    }

    public static Runnable getTimerTask() {
        Assert.notNull(timerTask);
        return timerTask;
    }

    public void schedu(int delay) {
        getTimer().scheduleAtFixedRate(getTimerTask(), delay, interval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void afterPropertiesSet() throws ClassNotFoundException {
        Class.forName(mysqlDriver);
        urls = new String[]{url, url1, url2};

        final int size = urls.length;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Gps schedu Task start.......");
                Connection[] connections = new Connection[size];
                Statement[] statements = new Statement[size];
                getConnections(connections, statements, null, size);
                if (connections[size - 1] == null) return;
                Calendar calendar = Calendar.getInstance();
                String sqlPrefix = "INSERT ignore YG_GPS.gps"+format.format(calendar.getTime())+"(devId,time, gps_lng, gps_lat) VALUES (";
                int count = 0;
                while (!gpsList.isEmpty() && count <= 1000000) {
                    Object[] gps = null;
                        gps = gpsList.poll();
                    count++;
                    int devId = (int) gps[0], index = devId % size;
                    StringBuffer sb = new StringBuffer(sqlPrefix);
                    sb.append(devId).append(",").append("from_unixtime(" + gps[3] + "),").append(gps[1]).append(",").append(gps[2]).append(")");
                    try {
                        statements[index].addBatch(sb.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < size; i++) {
                    try {
                        statements[i].executeBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        logger.error("insert gpss data is error....");
                    } finally {
                        closeDBC(connections[i], statements[i]);
                    }
                }
                System.gc();
                logger.debug("GPS write MySQL success........");
                Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        e.printStackTrace();
                        logger.error("gps save to MySQL Thread Exception." + e.getMessage());
                        getTimer().scheduleAtFixedRate(getTimerTask(), 5000, interval, TimeUnit.MILLISECONDS);
//                            t.run();
                    }
                });
            }
        };
        schedu(5000);
    }

    public void closeDBC(Connection conn, Statement stmt) {
        try {
            if (stmt != null) {
                stmt.clearWarnings();
                stmt.close();
            }
            if (conn != null) {
                conn.clearWarnings();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getConnections(final Connection[] connections, final Statement[] statements, final StringBuffer[] sbs, int size) {
        try {
            for (int i = 0; i < size; i++) {
                connections[i] = DriverManager.getConnection(urls[i], userName, password);
                statements[i] = connections[i].createStatement();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getConnection throw Exception.....");
        }
    }
}
