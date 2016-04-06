package com.revolut.sinap.netty;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jetbrains.annotations.Nullable;

public class SimpleHttpServer {
    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "TRACE");

        HttpHandler testHandler = request -> HttpUtils.createTextPlainHttpResponseUtf8(HttpResponseStatus.OK, "test success");

        HttpHandler dispatcher = new AbstractDispatchHttpHandler() {
            @Nullable
            @Override
            protected HttpHandler getHandler(FullHttpRequest request) {
                String uri = request.getUri();
                if ("/test".equals(uri)) {
                    return testHandler;
                }
                return null;
            }
        };

        NettyServer server = new HttpServerBuilder("test")
                .handler(dispatcher)
                .group(1, 4)
                .localAddress("127.0.0.1", 8080)
                .build();

        server.startSync().channel().closeFuture().syncUninterruptibly();
    }
}
