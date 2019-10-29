package com.zcq.noon;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import com.zcq.noon.util.ThreadPoolUtils;

/**
 * @author zhengchuqin
 * @version 1.0
 * @since 2019/10/08
 */
public class SocketServer implements ApplicationListener<ContextRefreshedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    @Autowired
    private SocketServerInitializer socketServerInitializer;

    private final static int PORT = 8555;

    private final static int SO_BACKLOG_VAL = 128;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        ThreadPoolUtils.execute(() -> {
            //处理客户端连接线程
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            //处理客户端io操作线程
            EventLoopGroup workGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap boot = new ServerBootstrap();
                boot.group(bossGroup, workGroup);
                boot.channel(NioServerSocketChannel.class);
                boot.childHandler(socketServerInitializer);
                boot.option(ChannelOption.SO_BACKLOG, SO_BACKLOG_VAL);
                //设置分配bytebuf时使用内存池
                boot.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                boot.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                ChannelFuture f = boot.bind(PORT).sync();
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                LOGGER.error("start server listening", e);
            } finally {
                workGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }, "socketServer");
        LOGGER.info("start server listening port " + PORT);
    }
}
