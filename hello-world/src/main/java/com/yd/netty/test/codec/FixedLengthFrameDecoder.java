package com.yd.netty.test.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 编写一个简单的ByteToMessageDecoder实现，有足够的数据可以读取时将产生固定大小的包，
 * 如果没有足够的数据可以读取，则会等待下一个数据块并再次检查是否可以产生一个完整包。
 *
 * @author Yd on 2018-07-06
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) throws Exception {
        while (in.readableBytes() >= frameLength) {
            ByteBuf buf = in.readBytes(frameLength);
            out.add(buf);
        }
    }

}