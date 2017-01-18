package com.yanguan.device.cmd;

import io.netty.channel.Channel;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.yanguan.device.model.Constant;

@Component("Sync_Time")
public class SynchroTime implements IProcess{

	
	@Override
    public void process(Channel channel, Map<String, Object> data) {

		long time=System.currentTimeMillis();
        int intTime=(int)(time/1000);
        StringBuffer sb = new StringBuffer(data.get("iType") + Constant.SPLIT_CHAR);
        sb.append(intTime).append(Constant.SPLIT_CHAR)
                .append(655);

        channel.writeAndFlush(sb.toString());
    }
}
