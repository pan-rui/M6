/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.yanguan.device.handle;

import com.yanguan.device.dao.DeviceMapper;
import com.yanguan.device.nio.Client;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * @author [*田园间*]   liaoxuqian@hotmail.com
 * @since version 1.0
 * @datetime 2015-11-19 05:19:04
 */
@Component
@ChannelHandler.Sharable
public class ServerHandle extends SimpleChannelInboundHandler<ByteBuf> {
    private final static Logger logger = Logger.getLogger(ServerHandle.class);
    @Autowired
    private DeviceMapper deviceMapper;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        logger.debug("Recieved cmd packet length: " + byteBuf.readShort());
        int iType=byteBuf.readShort();
        int mcc=byteBuf.readInt();
        int mnc=byteBuf.readInt();
        int lac=byteBuf.readInt();
        int cell=byteBuf.readInt();
        short verifyCode = byteBuf.readShort();
        Map<String,Object> city=null;
        city=deviceMapper.queryDeviceCity(String.valueOf(mcc), String.valueOf(mnc), String.valueOf(lac), String.valueOf(cell));
        if(city==null) city=deviceMapper.queryDeviceProvince(String.valueOf(mcc), String.valueOf(mnc), String.valueOf(lac));
        String cName= (String) city.get("cName");
        logger.info("query cityName is :"+cName);
        byte[] data=Client.cityMap.get(cName);
        if(data==null){
            if(!Client.cityMap.isEmpty()) data = (byte[]) Client.cityMap.values().toArray()[0];
        }
        ByteBuf byteBuf1= Unpooled.buffer();
        byteBuf1.writeShort(2 + 2 + data.length + 2);
        byteBuf1.writeShort(iType);
        byteBuf1.writeBytes(data);
        byteBuf1.writeShort(verifyCode);
        ChannelFuture future = channelHandlerContext.writeAndFlush(byteBuf1.array());
        future.channel().flush();
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isDone()) {
                    if (future.isSuccess()) {
                        logger.info("Send packet response to Agps client successfull.packet: ");
                        return;
                    }

                    if (future.cause() != null) {
                        logger.error("Send to Agps Client error", future.cause());
                    }
                }
            }
        });
    }

}
