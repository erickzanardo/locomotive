package org.eck;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

public class LocomotiveHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequest request;
    private Locomotive locomotive;
    private Wagon wagon;

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
            String method = request.getMethod().name().toUpperCase();
            String uri = request.getUri();

            this.wagon = locomotive.getWagon(method, uri);
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            FullHttpResponse response = null;
            if (this.wagon != null) {
                LocomotiveResponseWrapper resp = new LocomotiveResponseWrapper();
                this.wagon.process(null, resp);

                HttpResponseStatus status =
                        resp.status() != null ?
                                HttpResponseStatus.valueOf(resp.status()) : OK;

                response = new DefaultFullHttpResponse(
                        HTTP_1_1, httpContent.getDecoderResult().isSuccess() ? status : BAD_REQUEST,
                        Unpooled.copiedBuffer(resp.toString(), CharsetUtil.UTF_8));
            } else {
                response = new DefaultFullHttpResponse(
                        HTTP_1_1, NOT_FOUND);
            }

            ctx.writeAndFlush(response);
            // TODO Check for keep alive
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
