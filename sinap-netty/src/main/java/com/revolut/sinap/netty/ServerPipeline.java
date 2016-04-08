package com.revolut.sinap.netty;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LoggingHandler;

class ServerPipeline extends ChannelInitializer<SocketChannel> {
    private final LoggingHandler loggingHandler;
    private final ChannelInboundHandler serverHttpHandler;

    ServerPipeline(String name, HttpHandler httpHandler) {
        this.loggingHandler = new LoggingHandler(name + "-socket");
        this.serverHttpHandler = new ServerHttpHandler(name, httpHandler);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        // todo ReadTimeoutHandler, WriteTimeoutHandler

        // sharable
        // p.addLast(loggingHandler);

        // non-sharable
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpObjectAggregator(16384));

        // sharable
        p.addLast(loggingHandler);

        // sharable
        p.addLast(serverHttpHandler);
    }
}
