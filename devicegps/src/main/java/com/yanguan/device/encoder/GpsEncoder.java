package com.yanguan.device.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-27 11:06)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component
public class GpsEncoder extends MessageToMessageEncoder<String> {
    @Autowired
    private M6Encoder m6Encoder;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
        ByteBuf byteBuf= Unpooled.buffer();
        m6Encoder.encode(channelHandlerContext, s,byteBuf);
        ByteBuf sendBuf = Unpooled.copyShort(byteBuf.readableBytes()).writeBytes(byteBuf);
        list.add(sendBuf.retain());
    }
}
