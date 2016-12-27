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
    @Value("#{config['nio.coreSize']?:256}")
    private int coreSize;
    @Value("#{config['nio.maxSize']?:4096}")
    private int maxSize;
    @Value("#{config['nio.keepAlive']?:15000}")
    private long keepAlive;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private EventExecutor eventExecutor=null;
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
//        bossGroup = new NioEventLoopGroup(bossThreads);
//        workerGroup = new NioEventLoopGroup(workThreads);
//        ((NioEventLoopGroup) workerGroup).setIoRatio(workerEventLoopIORatio);
        bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1400, 65536))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.group(workerGroup);
        bootstrap.channel(NioDatagramChannel.class);
        ThreadPoolExecutor tpe=new ThreadPoolExecutor(coreSize,maxSize,keepAlive, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(maxSize));
        tpe.allowCoreThreadTimeOut(true);
        eventExecutor = new DefaultEventExecutor(tpe);
        logger.info("Initialized the Schedu serivce.");

    }

    public void start() {
        logger.info("Start Schedu Server .....");
        bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            public void initChannel(DatagramChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
//                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(10240, 0, 2, 0, 2));
//                pipeline.addLast("frameEncoder", new LengthFieldPrepender(2,false));//生成的长度值不包含长度本身的长度
                pipeline.addLast(gpsDecoder);
                pipeline.addLast(dataEncoder);
                pipeline.addLast(eventExecutor,serverHandle);
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
