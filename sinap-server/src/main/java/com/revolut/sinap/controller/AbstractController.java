package com.revolut.sinap.controller;

import com.revolut.sinap.netty.HttpHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class AbstractController<RQ, RS> implements HttpHandler {
    @Override
    public final FullHttpResponse handle(FullHttpRequest request) throws Exception {
        RQ req = parseRequest(request);
        RS resp = process(req);
        return formatResponse(resp);
    }

    protected abstract RQ parseRequest(FullHttpRequest request);

    protected abstract RS process(RQ req);

    protected abstract FullHttpResponse formatResponse(RS resp);
}
