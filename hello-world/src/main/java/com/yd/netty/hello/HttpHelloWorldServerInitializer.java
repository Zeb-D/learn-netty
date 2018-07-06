package com.yd.netty.hello;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import javax.net.ssl.SSLContext;

/**
 * @author Yd on 2018-06-29
 * @description
 */
public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SSLContext sslCtx;

    //private static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);
    //p.addLast(group, "handler", new HttpHelloWorldServerHandler2());//业务线程独立的线程池

    public HttpHelloWorldServerInitializer(SSLContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {

            p.addLast("ssl", new SslHandler(sslCtx.createSSLEngine()));
        }
        /*注意此处的超时时间不是客户端TCP连接的超时时间，而是服务器处理的时间，如果超时，那么就会触发handler里面的exceptionCaught */
        p.addLast(new ReadTimeoutHandler(10));//服务器端设置超时时间,单位：秒
        p.addLast(new WriteTimeoutHandler(10));//服务器端设置超时时间,单位：秒
        p.addLast(new HttpServerCodec());//对http通信数据进行编解码
        p.addLast(new HttpHelloWorldServerHandler()); //业务handler

    }
}
