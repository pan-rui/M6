package com.yanguan.device.nio;

import com.yanguan.device.dao.DeviceMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 定时调用AGps官户端数据
 * @Created: 潘锐 (2015-12-21 10:06)
 * $Rev: 758 $
 * $Author: panrui $
 * $Date: 2016-08-10 16:33:53 +0800 (周三, 10 八月 2016) $
 */
@Component
public class Client implements InitializingBean {
    private static final Logger logger = Logger.getLogger(Client.class);
    private static ScheduledExecutorService timer;
    private static Runnable timerTask;
    public volatile static ConcurrentHashMap<String, byte[]> cityMap = new ConcurrentHashMap();
    @Value("#{config['agps.host']}")
    private String host;
    @Value("#{config['agps.port']}")
    private int port;
    @Value("#{config['agps.cmd']}")
    private String cmd;
    @Value("#{config['agps.user']}")
    private String user;
    @Value("#{config['agps.pwd']}")
    private String pwd;

    @Value("#{config['agps.interval']}")
    private int interval;
    @Value("#{config['agps.city.request']}")
    private long requestInterval;
    @Value("#{config['agps.city.r']}")
    private long cityR;
    @Value("#{config['agps.province.r']}")
    private long provinceR;
    @Autowired
    private DeviceMapper deviceMapper;

    public synchronized static ScheduledExecutorService getTimer() {
        return timer == null ? (timer = Executors.newScheduledThreadPool(3)) : timer;
    }

    public static Runnable getTimerTask() {
        Assert.notNull(timerTask);
        return timerTask;
    }

    public void schedu(int delay) {
        getTimer().scheduleAtFixedRate(getTimerTask(), delay, interval, TimeUnit.MILLISECONDS);
    }

    public String getConf(Object lon,Object lat,String r) {
        return "cmd="+cmd+";user="+user+";pwd="+pwd+";lat="+lat+";lon="+lon+";pacc="+r+"\\n";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Map<String, Object>> citys = deviceMapper.queryAllCity();
        List<Map<String, Object>> provinces = deviceMapper.queryAllProvince();
        timerTask = new Runnable() {
            @Override
            public void run() {
                for(Map<String,Object> city:citys){
                    try {
                        Socket client = new Socket(host, port);
                        OutputStream out = client.getOutputStream();
                        InputStream in = client.getInputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        out.write(getConf(city.get("LNG"),city.get("LAT"),String.valueOf(cityR)).getBytes());
                        byte[] buf = new byte[4096];
                        int len = 0, offset = 0;
                        while ((len = in.read(buf)) > 0)
                            bos.write(buf, 0, len);
                        bos.flush();
                        bos.close();
                        in.close();
                        out.close();
                        client.close();
                        byte[] totalData = bos.toByteArray();
                        char[] chars = new char[2048];
            /* 去除请求头
            Content-Length: 1704
            Content-Type: application/ubx
             */
                        for (int i = 0; i < totalData.length; i++) {
                            chars[i] = (char) totalData[i];
                            if (i >= 8)
                                if (new String(chars, i - 7, 8).matches("/ubx\\r\\n\\r\\n")) {
                                    offset = i + 1;
                                    break;
                                }
                        }
                        logger.info("receive u-blox length:" + totalData.length + "\toffset is:" + offset);
                        byte[] data = new byte[totalData.length - offset];
                        System.arraycopy(totalData, offset, data, 0, data.length);
                        cityMap.put((String) city.get("CITY_NAME"), data);
                        Thread.sleep(requestInterval);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.error("request Thread is Interrupted..");
                    }
                };
                for(Map<String,Object> province:provinces){
                    try {
                        Socket client = new Socket(host, port);
                        OutputStream out = client.getOutputStream();
                        InputStream in = client.getInputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        out.write(getConf(province.get("LNG"),province.get("LAT"),String.valueOf(provinceR)).getBytes());
                        byte[] buf = new byte[4096];
                        int len = 0, offset = 0;
                        while ((len = in.read(buf)) > 0)
                            bos.write(buf, 0, len);
                        bos.flush();
                        bos.close();
                        in.close();
                        out.close();
                        client.close();
                        byte[] totalData = bos.toByteArray();
                        char[] chars = new char[2048];
            /* 去除请求头
            Content-Length: 1704
            Content-Type: application/ubx
             */
                        for (int i = 0; i < totalData.length; i++) {
                            chars[i] = (char) totalData[i];
                            if (i >= 8)
                                if (new String(chars, i - 7, 8).matches("/ubx\\r\\n\\r\\n")) {
                                    offset = i + 1;
                                    break;
                                }
                        }
                        logger.info("receive u-blox length:" + totalData.length + "\toffset is:" + offset);
                        byte[] data = new byte[totalData.length - offset];
                        System.arraycopy(totalData, offset, data, 0, data.length);
                        cityMap.put((String) province.get("PROVINCE_NAME"), data);
                        Thread.sleep(requestInterval);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.error("request Thread is Interrupted..");
                    }
                };
/*            //缓存到redis
            redis.putAGps(data);
            logger.info("saved AGps data success........byte length:"+data.length);*/
                //缓存到HBase
                Object result = null;
                if (result != null) logger.info("saved AGps to HBase success.......");
                else logger.error("saved AGps to HBase FAIL.......");
                Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        e.printStackTrace();
                        logger.error("AgpsClient Thread Exception.");
                        schedu(5000);
                    }
                });

            }
        };
        schedu(5000);
    }
}
