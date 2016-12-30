import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.AttributeKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-30 19:08)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class UdpByteToMessageEncode extends MessageToMessageEncoder<ByteBuf> {
    private static Log log = LogFactory.getLog(UdpByteToMessageEncode.class);
    public static final AttributeKey<InetSocketAddress> TARGET_ADDRESS = AttributeKey.valueOf("TARGET_ADDRESS");
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        InetSocketAddress ip = ctx.channel().attr(TARGET_ADDRESS).get();
        if (ip == null) {
            log.error("no server ip");
            return;
        }
        DatagramPacket packet = new DatagramPacket(msg, ip);
        msg.retain();
        out.add(packet);
    }
}