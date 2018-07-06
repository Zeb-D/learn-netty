package com.yd.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author Yd on 2018-07-06
 * @description
 */
public class HttpCompressionInitialzer extends ChannelInitializer<Channel> {
    private final boolean isClient;

    public HttpCompressionInitialzer(boolean isClient){
        this.isClient=isClient;
    }
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline  pipeline = channel.pipeline();
        if (isClient){
            pipeline.addLast("codec",new HttpClientCodec())
                    .addLast("decompressor",new HttpContentDecompressor());//处理来自服务器的压缩内容
        }else {
            pipeline.addLast("codec",new HttpServerCodec())
                    .addLast("compressor",new HttpContentCompressor());
        }
    }
}
