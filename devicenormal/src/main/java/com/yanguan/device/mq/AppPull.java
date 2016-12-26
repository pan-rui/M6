package com.yanguan.device.mq;

import com.yanguan.device.model.Constant;
import com.yanguan.device.handle.IdleHandler;
import com.yanguan.device.task.CmdWriteDB;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-21 12:17)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class AppPull implements MessageListener{
    private static final Logger logger = Logger.getLogger(AppPull.class);
    private int coreSize;
    private int keepAlive;
    private int maxSize;
    private int expire;
    private ExecutorService sendExecutor;
    private AppPush appPush;

    public void setAppPush(AppPush appPush) {
        this.appPush = appPush;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void init() {
        this.sendExecutor = new ThreadPoolExecutor(coreSize,maxSize,keepAlive, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(maxSize));
    }

    @Override
    public void onMessage(Message message) {        //不判断指令是否过期
        String data=null;
        try {
            data=((TextMessage)message).getText();

        } catch (JMSException e) {
            e.printStackTrace();
            logger.error("get message error..");
            return;
        }
        final String dataStr = data;
        String[] dataArr = data.split(",");
        sendExecutor.submit(new Runnable() {
            @Override
            public void run() {
                int devId = Integer.parseInt(dataArr[3]);
                Channel channel=IdleHandler.getConnectionMap().get(devId);
                final long currentMillis=System.currentTimeMillis();
                if(channel!=null && channel.isActive()&&channel.isOpen()&&channel.isWritable()){        //在线
                    channel.writeAndFlush(dataStr).addListener(new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(Future<? super Void> future) throws Exception {
                            if (future.isDone()) {
                                String resultCode=null;
                                if (future.isSuccess()) {
                                    IdleHandler.getDeviceStatuss().get(devId).setLastReceive(currentMillis);
                                    resultCode = "8";
                                    logger.info("Send packet to the device successfully.cmd: " +dataStr );
                                    return;
                                }
                                if (future.cause() != null) {
                                    resultCode = "9";
                                    appPush.sendMessage(devId,Constant.Push_Cmd_Fail,currentMillis,null);
                                    logger.error("Send packet to the device failed.cmd: " +dataStr);
                                }
                                synchronized(CmdWriteDB.cmdList) {
                                    CmdWriteDB.cmdList.add(resultCode + Constant.SPLIT_CHAR + dataStr);
                                }
                            }
                        }
                    });

                }else {     //离线
                    int iType = Integer.parseInt(dataArr[0]);
//                    int cmdType = dataArr[3].matches("^\\d+$")?Integer.parseInt(dataArr[3]):0;
                    if (iType == 29) {
                            Jedis jedis= Constant.jedisPool.getResource();
                            jedis.hset(Constant.Device_Cmd_Cache, dataArr[4], dataStr);
                            jedis.close();
                    }
                    IdleHandler.getConnectionMap().remove(devId);
                    appPush.sendMessage(devId,Constant.Push_OffineLine,currentMillis,null);
                    logger.info("app send Cmd is Fail.For device offineLine.the cmd:"+dataStr);
                }
            }
        });
    }
/*    public void run() {
        switch (Integer.parseInt(dataArr[0])) {
            case 20:
                process0(dataArr);
                break;
            case 21:
                process1(dataArr);
                break;
        }
    }*/
    public void process0(String[] dataArry) {

    }

//消息码,项目名,软件版本,指令类型,设备码,参数....校验码
    public void process1(String[] dataArry) {
        int cmdType = Integer.parseInt(dataArry[3]);
        int devId = Integer.parseInt(dataArry[4]);
        int value = Integer.parseInt(dataArry[5]);
        switch (cmdType) {
            case 1:

        }
    }
}
