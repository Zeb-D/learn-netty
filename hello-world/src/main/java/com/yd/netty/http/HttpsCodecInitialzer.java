package com.yd.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * @author Yd on 2018-07-06
 * @description
 */
public class HttpsCodecInitialzer extends ChannelInitializer<Channel> {
    private final SSLContext context;
    private final boolean isClient;

    public HttpsCodecInitialzer(SSLContext context,boolean isClient){
        this.context = context;
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        SSLEngine engine = context.createSSLEngine();channel.alloc();
        pipeline.addFirst("ssl",new SslHandler(engine));//使用HTTPS
        if (isClient){
            pipeline.addLast(new HttpClientCodec());
        }else {
            pipeline.addLast(new HttpServerCodec());
        }

    }
}
