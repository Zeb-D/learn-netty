package com.yd.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * EmbeddedChannel-测试ChannelHandler链
 * 可用于编解码器、channelHandler
 * 方法	职责
 * writeInbound(Object... msgs)	将入站消息写入到EmbeddedChannel中
 * readInbound()	从EmbeddedChannel中读取一个入站消息，任何返回的消息都穿过了整个ChannelPipeLine
 * writeOutbound(Object... msgs)	将出站消息写入到EmbeddedChannel中
 * readOutbound()	从EmbeddedChannel中读取一个出站消息，任何返回的消息都穿过了整个ChannelPipeLine
 *
 * @author Yd on 2018-07-06
 */
public class EmBeddedChannelTest {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.buffer();
        for (int i = 0; i < 3; i++) {
            byteBuf.writeInt(i);
        }

        EmbeddedChannel embeddedChannel = new EmbeddedChannel();

        //获取channelPipeLine
        ChannelPipeline channelPipeline = embeddedChannel.pipeline();
        channelPipeline.addLast(new SimpleChannelInBoundHandlerTest());
        channelPipeline.addFirst(new DecodeTest());

        //写入测试数据
        embeddedChannel.writeInbound(byteBuf);

        System.out.println("embeddedChannel readInbound:" + embeddedChannel.readInbound());
        System.out.println("embeddedChannel readInbound:" + embeddedChannel.readInbound());
        System.out.println("embeddedChannel readInbound:" + embeddedChannel.readInbound());

    }


}

//解码器
class DecodeTest extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) {
            out.add(in.readInt());
        }
    }
}

//channelHandler
class SimpleChannelInBoundHandlerTest extends SimpleChannelInboundHandler {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Received message:" + msg);
        System.out.println("Received Finished!");
        ctx.fireChannelRead(msg);
    }
}