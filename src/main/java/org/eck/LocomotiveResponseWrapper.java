package org.eck;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LocomotiveResponseWrapper {
    private HttpContent httpContent;
    private ChannelHandlerContext ctx;

    private StringBuilder buff = new StringBuilder();
    private Integer status;
    private Map<String, String> headers = new HashMap<String, String>();

    public LocomotiveResponseWrapper(HttpContent httpContent,
            ChannelHandlerContext ctx) {
        this.httpContent = httpContent;
        this.ctx = ctx;
    }

    public void append(String value) {
        buff.append(value);
    }

    public String toString() {
        return buff.toString();
    }

    public Integer status() {
        return status;
    }

    public void status(int status) {
        this.status = status;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public void contentType(String contentType) {
        headers.put("Content-Type", contentType);
    };

    public void send() {
        send(null);
    }

    public void send(String content) {
        if (content != null) {
            append(content);
        }

        // Status
        HttpResponseStatus status = status() != null ? HttpResponseStatus
                .valueOf(status()) : OK;

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                httpContent.getDecoderResult().isSuccess() ? status
                        : BAD_REQUEST, Unpooled.copiedBuffer(this.toString(),
                        CharsetUtil.UTF_8));

        // Headers
        Set<Entry<String, String>> entrySet = headers().entrySet();
        for (Entry<String, String> entry : entrySet) {
            response.headers().add(entry.getKey(), entry.getValue());
        }

        ctx.writeAndFlush(response);
        // TODO Check for keep alive
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(
                ChannelFutureListener.CLOSE);
    }
}
