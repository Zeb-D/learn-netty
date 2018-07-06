package com.yd.netty.test.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 将已接收的数据flush()后将从ByteBuf读取所有整数并调用Math.abs(...)
 * 完成后将字节写入ChannelPipeline中下一个ChannelHandler的ByteBuf中
 *
 * @author Yd on 2018-07-06
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg,
                          List<Object> out) throws Exception {
        while (msg.readableBytes() >= 4) {
            int value = Math.abs(msg.readInt());
            out.add(value);
        }
    }
}
