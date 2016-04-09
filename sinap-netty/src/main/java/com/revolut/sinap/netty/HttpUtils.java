package com.revolut.sinap.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.jetbrains.annotations.Nullable;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpUtils {
    private HttpUtils() {
    }

    public static FullHttpResponse createTextPlainHttpResponseUtf8(HttpResponseStatus status, String message) {
        ByteBuf buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
        FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, status, buf);
        resp.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        resp.headers().set(CONTENT_LENGTH, buf.readableBytes());
        return resp;
    }

    public static FullHttpResponse createJsonHttpResponse(HttpResponseStatus status, byte[] responseBody) {
        ByteBuf buf = Unpooled.copiedBuffer(responseBody);
        DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
        resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        resp.headers().set(HttpHeaders.Names.CONTENT_LENGTH, responseBody.length);
        return resp;
    }

    /**
     * Note: this method does not release ReferenceCounted request
     *
     * @param request
     * @return
     */
    @Nullable
    public static byte[] getBody(FullHttpRequest request) {
        ByteBuf content = request.content();
        if (content == null) {
            return null;
        }
        int capacity = content.capacity();
        byte[] body = new byte[capacity];
        content.readBytes(body);
        return body;
    }
}
