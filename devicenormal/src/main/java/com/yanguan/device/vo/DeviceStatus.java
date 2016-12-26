package com.yanguan.device.vo;

import io.netty.channel.Channel;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-19 18:10)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class DeviceStatus {

private long onLineTime=System.currentTimeMillis();
private long lastSendTime=0;
private long lastReceive=0;
private int devId;

    public DeviceStatus(int devId) {
        this.devId = devId;
    }

    public DeviceStatus(long lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public long getOnLineTime() {
        return onLineTime;
    }

    public void setOnLineTime(long onLineTime) {
        this.onLineTime = onLineTime;
    }

    public long getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(long lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public long getLastReceive() {
        return lastReceive;
    }

    public void setLastReceive(long lastReceive) {
        this.lastReceive = lastReceive;
    }
}
