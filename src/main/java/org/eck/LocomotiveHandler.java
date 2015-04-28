package org.eck;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import org.eck.exceptions.LocomotiveException;
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
                LocomotiveResponseWrapper resp = new LocomotiveResponseWrapper(
                        httpContent, ctx);
                try {
                    for (LocomotiveMiddleware middleware : locomotive
                            .middlewares()) {
                        middleware.execute(requestWrapper, resp);
                        if (requestWrapper.isProcessed()) {
                            break;
                        }
                    }
                } catch (LocomotiveException e) {
                    requestWrapper.processed();
                    resp.status(e.code());
                    resp.send(e.getMessage());
                } catch (Exception e) {
                    requestWrapper.processed();
                    resp.status(500);
                    resp.send(e.getMessage());
                    e.printStackTrace();
                }

                if (!requestWrapper.isProcessed()) {
                    resp.status(404);
                    resp.send();
                }
            }
        }
    }
}
