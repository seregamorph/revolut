package com.revolut.sinap.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
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

    /**
     * Get uri without parameters.<br/>
     * <pre>
     * java servlet api: HttpServletRequest.getRequestURI()
     * for request
     * GET /abc/def?a=b HTTP/1.0
     * will return
     * /abc/def
     * </pre>
     *
     * @param request
     * @return
     */
    public static String getRequestUri(FullHttpRequest request) {
        // with params (query string)
        String fullUri = request.getUri();
        return getRequestUri(fullUri);
    }

    static String getRequestUri(String fullUri) {
        int qpos;
        if (fullUri == null || (qpos = fullUri.indexOf('?')) < 0) {
            return fullUri;
        }
        return fullUri.substring(0, qpos);
    }
}
