package com.yanguan.device.nio;


import com.yanguan.device.decoder.GpsDecoder;
import com.yanguan.device.decoder.M6Decoder;
import com.yanguan.device.encoder.M6Encoder;
import com.yanguan.device.handle.ServerHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 14:36)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Controller
public class NettyServer {
    private static final Logger logger = Logger.getLogger(NettyServer.class);
    @Value("#{config['nio.port']}")
    private int prot = 10001;
    @Value("#{config['nio.work.threads']}")
    private int workThreads = 256;
    @Value("#{config['nio.work.io.ratio']}")
    private int workerEventLoopIORatio;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    @Autowired
    private GpsDecoder gpsDecoder;
    @Autowired
    private DatagramPacketEncoder dataEncoder;
    @Autowired
    private ServerHandle serverHandle;

    @PostConstruct
    public void initialize() {
         workerGroup = new NioEventLoopGroup(workThreads);
        ((NioEventLoopGroup) workerGroup).setIoRatio(workerEventLoopIORatio);
        bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_BROADCAST,true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_RCVBUF, 1024*64);
        bootstrap.group(workerGroup);
        bootstrap.channel(NioDatagramChannel.class);
        logger.info("Initialized the Schedu serivce.");

    }

    public void start() {
        logger.info("Start Schedu Server .....");
        bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            public void initChannel(DatagramChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(gpsDecoder);
                pipeline.addLast(dataEncoder);
                pipeline.addLast(serverHandle);
            }
        });

        try {
            ChannelFuture future=bootstrap.bind(prot).sync();
            future.channel().closeFuture().sync();      //等待服务端监听端口关闭
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }finally {
            shutdown();
        }
        logger.info("Schedu Server started Success");
    }

    @PreDestroy
    public void shutdown(){
        logger.info("Shutdown the nio service...");

        // to do
        workerGroup.shutdownGracefully();
        logger.info("The nio serivce shutdown successfully");
    }

}
