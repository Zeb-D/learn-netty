package com.yd.netty.test.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * 有时候传输的入站或出站数据不够，通常这种情况也需要处理，例如抛出一个异常。
 * 这可能是你错误的输入或处理大的资源或其他的异常导致。我们来写一个实现，如果输入字节超出限制长度就抛出TooLongFrameException
 *
 * @author Yd on 2018-07-06
 */
public class FrameChunkDecoder extends ByteToMessageDecoder {

    // 限制大小
    private final int maxFrameSize;

    public FrameChunkDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) throws Exception {
        // 获取可读字节数
        int readableBytes = in.readableBytes();
        // 若可读字节数大于限制值,清空字节并抛出异常
        if (readableBytes > maxFrameSize) {
            in.clear();
            throw new TooLongFrameException();
        }
        // 读取ByteBuf并放到List中
        ByteBuf buf = in.readBytes(readableBytes);
        out.add(buf);
    }

}