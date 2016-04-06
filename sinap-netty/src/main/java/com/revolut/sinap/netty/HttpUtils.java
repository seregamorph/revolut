package com.revolut.sinap.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

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
}
