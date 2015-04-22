package org.eck;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import java.util.Map.Entry;
import java.util.Set;

import org.eck.middlewares.LocomotiveMiddleware;

public class LocomotiveHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequest request;
    private Locomotive locomotive;
    private LocomotiveRequestWrapper requestWrapper;

    public LocomotiveHandler(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof HttpRequest) {
            // Deal with request, parse url and other things
            HttpRequest request = this.request = (HttpRequest) msg;
            String uri = request.getUri();
            if (uri.contains("?")) {
                uri = uri.substring(0, uri.indexOf("?"));
            }

            String pattern = locomotive.getUriPattern(uri);
            this.requestWrapper = new LocomotiveRequestWrapper(this.request,
                    uri, pattern);
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                requestWrapper.body(content.toString(CharsetUtil.UTF_8));
            }

            if (msg instanceof LastHttpContent) {
                FullHttpResponse response = null;

                LocomotiveResponseWrapper resp = new LocomotiveResponseWrapper();
                for (LocomotiveMiddleware middleware : locomotive.middlewares()) {
                    middleware.execute(requestWrapper, resp);
                    if (requestWrapper.isProcessed()) {
                        break;
                    }
                }

                if (requestWrapper.isProcessed()) {
                    // Status
                    HttpResponseStatus status = resp.status() != null ? HttpResponseStatus
                            .valueOf(resp.status()) : OK;

                    response = new DefaultFullHttpResponse(HTTP_1_1,
                            httpContent.getDecoderResult().isSuccess() ? status
                                    : BAD_REQUEST, Unpooled.copiedBuffer(
                                    resp.toString(), CharsetUtil.UTF_8));

                    // Headers
                    Set<Entry<String, String>> entrySet = resp.headers()
                            .entrySet();
                    for (Entry<String, String> entry : entrySet) {
                        response.headers()
                                .add(entry.getKey(), entry.getValue());
                    }
                } else {
                    response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
                }

                ctx.writeAndFlush(response);
                // TODO Check for keep alive
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(
                        ChannelFutureListener.CLOSE);
            }
        }
    }
}
