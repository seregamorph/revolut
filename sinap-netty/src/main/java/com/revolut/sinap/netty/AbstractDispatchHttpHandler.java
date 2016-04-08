package com.revolut.sinap.netty;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Simple dispatcher by request uri
 */
public abstract class AbstractDispatchHttpHandler implements HttpHandler {
    @Override
    public final FullHttpResponse handle(FullHttpRequest request) throws Exception {
        String requestUri = HttpUtils.getRequestUri(request);
        HttpHandler handler = getHandler(requestUri);
        if (handler == null) {
            return handleMissingHandler(requestUri);
        }
        return handler.handle(request);
    }

    protected FullHttpResponse handleMissingHandler(String requestUri) {
        return HttpUtils.createTextPlainHttpResponseUtf8(HttpResponseStatus.NOT_FOUND, requestUri + " not found");
    }

    /**
     * Get handler by uri/headers/etc.
     *
     * @param requestUri
     * @return
     */
    @Nullable
    protected abstract HttpHandler getHandler(String requestUri);
}
