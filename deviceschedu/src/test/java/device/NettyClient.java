package device;

import com.yanguan.device.decoder.M6Decoder;
import com.yanguan.device.handle.ScheduServerHandle;
import com.yanguan.device.util.ByteConvertUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 16:06)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class NettyClient {
    private static final Logger logger = Logger.getLogger(NettyClient.class);
//    public static String HOST = "192.168.199.198";
//    public static String HOST = "119.29.162.74";
    public static String HOST = "192.168.199.39";
    public static int PORT = 19870;

    public static Bootstrap bootstrap = getBootstrap();
    public static Channel channel = getChannel(HOST, PORT);

    /**
     * 初始化Bootstrap
     *
     * @return
     */
    public static final Bootstrap getBootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(10240, 0, 2, 0, 2));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                pipeline.addLast("decoder", new M6Decoder());
//                pipeline.addLast("encoder", new M6Encoder());
                pipeline.addLast(new ByteArrayEncoder());
                pipeline.addLast("handler", new ScheduServerHandle());
            }
        });
        b.option(ChannelOption.SO_KEEPALIVE, true);
        return b;
    }

    public static final Channel getChannel(String host, int port) {
        Channel channel = null;
        try {
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            logger.error(String.format("连接Server(IP[%s],PORT[%s])失败", host, port), e);
            return null;
        }
        return channel;
    }

    public static void sendMsg(String msg) throws Exception {
        if (channel != null) {
            channel.writeAndFlush(msg).sync();
        } else {
            logger.warn("消息发送失败,连接尚未建立!");
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            long t0 = System.nanoTime();
//            for (int i = 0; i < 100000; i++) {
//                NettyClient.sendMsg("1000,?,ab,618000000000015,135554345,");
//            }
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeShort(7);
//                    .writeByte(255);
//            byteBuf.writeBytes(ByteConvertUtil.shortToByteArry((short)0));
//            byteBuf.writeBytes(ByteConvertUtil.longToByteArry(Long.parseLong("618000000000015")));
            byteBuf.writeShort(620)
                    .writeShort(10000)
//                    .writeInt(2000000)
                    .writeLong(618000000000005l)
//                    .writeShort(30000)
//                    .writeShort(40000)
//                    .writeShort(115).writeInt(2435352).writeShort(23).writeInt(235234)
//                    .writeByte(0)
                    .writeBytes("YG620".getBytes())
                    .writeBytes("T1S".getBytes())
                    .writeBytes("V1".getBytes())
                    .writeByte(22)
                    .writeBytes("prf489ew000880990000".getBytes())
                    .writeShort(23545);
/*            System.out.println(byteBuf.readableBytes());
            byte[] bytes = new byte[5];
            byteBuf.readBytes(bytes);
            byte[] b = new byte[2];
            System.arraycopy(bytes,3,b,0,2);
            System.out.println(ByteConvertUtil.byteArryToChars(b));*/
            NettyClient.channel.writeAndFlush(byteBuf);
            long t1 = System.nanoTime();
            System.out.println((t1 - t0) / 1000000.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
