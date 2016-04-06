package com.revolut.sinap.netty;

public class HttpServerBuilder extends NettyServerBuilder {
    private HttpHandler httpHandler;

    public HttpServerBuilder(String name) {
        super(name);
    }

    public HttpServerBuilder handler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
        return this;
    }

    @Override
    ServerPipeline getServerPipeline() {
        return new ServerPipeline(name(), httpHandler);
    }
}
