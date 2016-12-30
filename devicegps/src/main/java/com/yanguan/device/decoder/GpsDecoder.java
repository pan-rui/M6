package com.yanguan.device.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-27 12:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component
public class GpsDecoder extends MessageToMessageDecoder<DatagramPacket>{
    @Autowired
    private M6Decoder m6Decoder;
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf byteBuf=msg.content();
        byteBuf.readShort();
        m6Decoder.decode(ctx,byteBuf,out);
        msg.retain();
        Map<String, Object> data = (Map<String, Object>) out.get(0);
        data.put("sender", msg.sender());
        data.put("recipient", msg.recipient());
    }
}
