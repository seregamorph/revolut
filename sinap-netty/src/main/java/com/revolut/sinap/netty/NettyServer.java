package com.revolut.sinap.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class NettyServer {
    private final String name;
    private final ServerBootstrap bootstrap;

    private final Logger logger;

    public NettyServer(String name, ServerBootstrap bootstrap) {
        this.name = name;
        this.bootstrap = checkBootstrap(bootstrap);

        String loggerName = NettyServer.class.getName() + (InnerUtils.isBlank(name) ? "" : "." + name);
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    private static ServerBootstrap checkBootstrap(ServerBootstrap bootstrap) {
        Objects.requireNonNull(bootstrap, "bootstrap");
        Objects.requireNonNull(bootstrap.group(), "bootstrap group is not set");
        Objects.requireNonNull(bootstrap.childGroup(), "bootstrap childGroup is not set");

        return bootstrap;
    }

    public static ServerBootstrap newDefaultBootstrap() {
        return new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 50)
                .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
    }

    public final String name() {
        return name;
    }

    public ChannelFuture startSync() {
        logger.warn("\n" +
                ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n" +
                "Starting server {}", this);
        ChannelFuture future;
        try {
            future = this.bootstrap
                    .bind()
                    .syncUninterruptibly();
        } catch (Exception e) {
            logger.error("Error while starting server " + name(), e);
            throw new RuntimeException(e);
        }
        // there can be dynamic port in bootstrap (0) - log fact port
        logger.warn("Started server on " + future.channel().localAddress() + "\n" +
                "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return future;
    }

    public void shutdownSync() {
        logger.warn("\n" +
                ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n" +
                "Shutting down server {}", this);
        Future<?> groupShutdown;
        Future<?> childGroupShutdown;
        try {
            groupShutdown = bootstrap.group().shutdownGracefully();
        } finally {
            childGroupShutdown = bootstrap.childGroup().shutdownGracefully();
        }
        groupShutdown.awaitUninterruptibly();
        childGroupShutdown.awaitUninterruptibly();
        logger.warn("Server successfully shut down\n" +
                "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public String toString() {
        return "NettyServer{" +
                "name='" + name + "'" +
                ", bootstrap=" + bootstrap +
                '}';
    }
}
