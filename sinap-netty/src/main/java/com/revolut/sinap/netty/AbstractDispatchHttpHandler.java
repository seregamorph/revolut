package com.revolut.sinap.netty;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDispatchHttpHandler implements HttpHandler {
    @Override
    public final FullHttpResponse handle(FullHttpRequest request) throws Exception {
        HttpHandler handler = getHandler(request);
        if (handler == null) {
            return handleMissingHandler(request);
        }
        return handler.handle(request);
    }

    protected FullHttpResponse handleMissingHandler(FullHttpRequest request) {
        String uri = request.getUri();
        return HttpUtils.createTextPlainHttpResponseUtf8(HttpResponseStatus.NOT_FOUND, uri + " not found");
    }

    /**
     * Get handler by uri/headers/etc.
     *
     * @param request
     * @return
     */
    @Nullable
    protected abstract HttpHandler getHandler(FullHttpRequest request);
}
