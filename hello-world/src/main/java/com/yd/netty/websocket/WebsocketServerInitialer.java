package com.yd.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.*;

/**
 * @author Yd on 2018-07-06
 * @description
 */
public class WebsocketServerInitialer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(8*1024))
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                .addLast(new TextFrameHandler())
                .addLast(new BinaryFrameHandler())
                .addLast(new ContinuationFrameHandler());
    }
}

final class TextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        //todo
    }
}

final class BinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame>{
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, BinaryWebSocketFrame binaryWebSocketFrame) throws Exception {

    }
}

final class ContinuationFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame>{

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, ContinuationWebSocketFrame continuationWebSocketFrame) throws Exception {

    }
}
