package org.eck;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class LocomotiveInitializer extends ChannelInitializer<SocketChannel> {
    private Locomotive locomotive;

    public LocomotiveInitializer(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new LocomotiveHandler(locomotive));
    }
}
