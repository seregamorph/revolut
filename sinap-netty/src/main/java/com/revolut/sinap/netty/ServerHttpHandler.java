package com.revolut.sinap.netty;

import com.revolut.sinap.http.HttpHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Objects;

class ServerHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String name;
    private final HttpHandler handler;

    private final Logger logger;

    ServerHttpHandler(String name, HttpHandler handler) {
        this.name = name;
        this.handler = Objects.requireNonNull(handler, "handler");

        String loggerName = getClass().getName() + (InnerUtils.isBlank(name) ? "" : "." + name);
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    final String name() {
        return name;
    }

    @Override
    protected final void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        logger.debug("Got request {}", request);

        MDC.put("ip", InnerUtils.getHostAddress(ctx.channel().remoteAddress()));
        try {
            FullHttpResponse response = handler.handle(request);
            logger.debug("Sending response {}", response);
            ctx.writeAndFlush(response);
        } catch (Throwable e) {
            FullHttpResponse response = handleError(e);
            ctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE);
        } finally {
            MDC.remove("ip");
        }
    }

    private FullHttpResponse handleError(Throwable e) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in [" + name() + "] " + ctx + ", closing connection", cause);
        ctx.close();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + "'" +
                ", handler=" + handler +
                "}";
    }
}
