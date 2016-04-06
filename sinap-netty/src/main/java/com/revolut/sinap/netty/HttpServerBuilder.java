package com.revolut.sinap.netty;

import com.revolut.sinap.http.HttpHandler;

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
    protected ServerPipeline getServerPipeline() {
        return new ServerPipeline(name(), httpHandler);
    }
}
