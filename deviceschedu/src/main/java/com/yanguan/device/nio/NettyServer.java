package com.yanguan.device.nio;


import com.yanguan.device.decoder.M6Decoder;
import com.yanguan.device.encoder.M6Encoder;
import com.yanguan.device.handle.ScheduServerHandle;
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
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
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
    private int prot = 19870;
    @Value("#{config['nio.work.threads']}")
    private int workThreads = 256;
    @Value("#{config['nio.work.io.ratio']}")
    private int workerEventLoopIORatio;
    @Value("#{config['nio.coreSize']?:10}")
    private int coreSize;
    @Value("#{config['nio.maxSize']?:4096}")
    private int maxSize;
    @Value("#{config['nio.keepAlive']?:30000}")
    private long keepAlive;
    protected static final int bossThreads = Runtime.getRuntime().availableProcessors() * 2; //默认
    private ServerBootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;
    private EventExecutor eventExecutor=null;

    @Autowired
    private ScheduServerHandle scheduServerHandle;
    @Autowired
    private M6Decoder m6Decoder;
    @Autowired
    private M6Encoder m6Encoder;
    @PostConstruct
    public void initialize() {
        bossGroup = new EpollEventLoopGroup(bossThreads);
        workerGroup = new EpollEventLoopGroup(workThreads);
        ((EpollEventLoopGroup) workerGroup).setIoRatio(workerEventLoopIORatio);
//        bossGroup = new NioEventLoopGroup(bossThreads);
//        workerGroup = new NioEventLoopGroup(workThreads);
//        ((NioEventLoopGroup) workerGroup).setIoRatio(workerEventLoopIORatio);
        bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_REUSEADDR, true)
                .option(EpollChannelOption.SO_REUSEPORT, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(EpollServerSocketChannel.class);
//        bootstrap.channel(NioServerSocketChannel.class);
        ThreadPoolExecutor tpe=new ThreadPoolExecutor(coreSize,maxSize,keepAlive, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(maxSize));
        tpe.allowCoreThreadTimeOut(true);
        eventExecutor = new DefaultEventExecutor(tpe);
        logger.info("Initialized the Schedu serivce.");

    }

    public void start() {
        logger.info("Start Schedu Server .....");
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(10240, 0, 2, 0, 2));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(2,false));//生成的长度值不包含长度本身的长度
                pipeline.addLast(m6Decoder);
                pipeline.addLast(m6Encoder);
                pipeline.addLast(eventExecutor,scheduServerHandle);
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
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("The nio serivce shutdown successfully");
    }

}
