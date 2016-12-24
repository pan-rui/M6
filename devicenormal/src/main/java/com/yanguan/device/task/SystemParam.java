package com.yanguan.device.task;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by panrui on 2016/4/12.
 */
/*@Component
public class SystemParam implements Task,Runnable {
    private final Logger logger = Logger.getLogger(SystemParam.class);
    private Task.TaskType taskType;
    @Autowired
    private DeviceMapper deviceMapper;
    private static CopyOnWriteArrayList<String> updates = new CopyOnWriteArrayList();
    private static BlockingQueue<Integer> queue;
    public static boolean isUpdate=false;
    public SystemParam() {
        this.taskType = new Task.TaskType("systemParam");
    }

    public static BlockingQueue<Integer> getQueue() {
        return queue;
    }

    public static CopyOnWriteArrayList<String> getUpdates() {
        return updates;
    }
    *//*    public SystemParam(TaskType taskType) {
            this.taskType = taskType;
        }*//*
    @Override
    public void process(TaskType taskType) {
        List<Map<String, Object>> deviceList = deviceMapper.queryAllInTab(Constant.DEVICE_TABLE_INFO);
        for(Integer devId:)
            updates.add(String.valueOf(devId));
        Map<Integer, DeviceConnection> clients =NioClientManager.getClients();
        queue = new ArrayBlockingQueue(100000000,true,clients.keySet());
        this.taskType =taskType;
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        logger.info("系统参数更新任务启动........"+queue.size());
        isUpdate=true;
        thread.start();
    }

    @Override
    public void run() {
        DevParams devParams = (DevParams) taskType.getData();
        try {
            if (devParams == null)
                devParams = deviceService.queryDevParams();
            Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    t.run();
//                    t.setDaemon(true);
                }
            });
            String formatedParams = formatParams(devParams);
            logger.info("系统参数为："+formatedParams);
            int count=updates.size();
            long stTime=System.currentTimeMillis();
            while (count-updates.size()>0||System.currentTimeMillis()-stTime<48*3600*1000) {
                Integer devId = queue.poll(48, TimeUnit.HOURS);
                if(devId==null) continue;
                if (!updates.contains(String.valueOf(devId))) continue;
                DeviceConnection connection = NioClientManager.getClients().get(devId);
                io.netty.channel.Channel channel = connection.channel();
                if(channel==null||!channel.isActive() || !channel.isOpen() || !channel.isWritable())
                    continue;
                processSystemParams(channel,devParams,devId,new String[]{formatedParams});
//                Thread.sleep(180000l);
                stTime=System.currentTimeMillis();
            }
        } catch (DeviceException e) {
            logger.error("查询系统参数错误");
            e.printStackTrace();
        } catch (InterruptedException e) {
            logger.error("更新系统参数任务中断");
            run();
            e.printStackTrace();
        }
        isUpdate=false;
    }

    private void processSystemParams(Channel channel, DevParams params, Integer devId, String[] content) {
        NioPacket response = new NioPacket();
        response.setProduct(params.getProduct());
        response.setVersion(params.getVersion());
        response.setType(PacketType.TYPE_SYS_PARAMETER);
        response.setDeviceKey(String.valueOf(devId));
        response.setContent(content);
//        try {
            // response the device
            channel.writeAndFlush(response);
            logger.info("发送系统参数到设备:"+devId);
*//*        } catch (Throwable e) {
            logger.error("设备更新系统参数失败："+devId);
        }*//*
    }

    private String formatParams(DevParams params) {
        if (params != null) {
            StringBuilder sb = new StringBuilder("0"+NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getRtGpsInterval());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getFtGpsInterval());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getUpRetryTimes());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getWaitSleepTime());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getGpsRebootTime());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getNetRetryTimes());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getNetRebootInterval());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getDevRebootTimes());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getHeartbeatInterval());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getSenseLevel1());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getSenseLevel2());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getSenseLevel3());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getSenseLevel4());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getSenseLevel5());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getShakeStTime1());
            sb.append(NioPacket.CONTENT_SPLIT_CHAR);
            sb.append(params.getShakeEndTime1());

            sb.append(NioPacket.CONTENT_SPLIT_CHAR+params.getShakeStTime2()).append(NioPacket.CONTENT_SPLIT_CHAR).append(params.getShakeEndTime2())
                    .append(NioPacket.CONTENT_SPLIT_CHAR).append(params.getShakeStTime3()).append(NioPacket.CONTENT_SPLIT_CHAR)
                    .append(params.getShakeEndTime3()).append(NioPacket.CONTENT_SPLIT_CHAR).append(params.getShakeStTime4()).append(NioPacket.CONTENT_SPLIT_CHAR).append(params.getShakeEndTime4())
                    .append(NioPacket.CONTENT_SPLIT_CHAR).append(params.getShakeStTime5()).append(NioPacket.CONTENT_SPLIT_CHAR).append(params.getShakeEndTime5());

            return sb.toString();
        }

        return null;
    }
}*/
