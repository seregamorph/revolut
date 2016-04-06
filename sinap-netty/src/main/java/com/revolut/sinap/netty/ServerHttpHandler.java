package com.revolut.sinap.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.FileNotFoundException;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Sharable
class ServerHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String name;
    private final HttpHandler handler;

    private final Logger logger;

    ServerHttpHandler(String name, HttpHandler handler) {
        super(FullHttpRequest.class);
        this.name = name;
        this.handler = Objects.requireNonNull(handler, "handler");

        String loggerName = getClass().getName() + (InnerUtils.isBlank(name) ? "" : "." + name);
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    final String name() {
        return name;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        logger.debug("Got request {}", request);

        if (HttpHeaders.is100ContinueExpected(request)) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }

        FullHttpResponse response;
        MDC.put("ip", InnerUtils.getHostAddress(ctx.channel().remoteAddress()));
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        try {
            response = handler.handle(request);
        } catch (Throwable e) {
            response = handleError(e);
            // reset keepalive to false anyway
            keepAlive = false;
        } finally {
            MDC.remove("ip");
        }

        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        logger.debug("Sending response {}", response);
        logger.debug("Keep alive={}", keepAlive);

        if (!keepAlive) {
            response.headers().set(CONNECTION, HttpHeaders.Values.CLOSE);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }
    }

    private FullHttpResponse handleError(Throwable e) {
        logger.error("Error while handling request", e);
        HttpResponseStatus status = getResponseStatus(e);
        // this is internal server by design - give it error string
        String message = e.toString();
        return HttpUtils.createTextPlainHttpResponseUtf8(status, message);
    }

    private static HttpResponseStatus getResponseStatus(Throwable e) {
        if (e instanceof FileNotFoundException) {
            return HttpResponseStatus.NOT_FOUND;
        } else if (e instanceof IllegalArgumentException) {
            return HttpResponseStatus.BAD_REQUEST;
        } else {
            return HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }
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
