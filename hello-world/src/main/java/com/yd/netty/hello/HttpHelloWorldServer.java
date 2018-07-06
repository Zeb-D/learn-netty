package com.yd.netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 */
public final class HttpHelloWorldServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SSLContext sslCtx;
        if (SSL) {
//            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SSLContext.getDefault();
//                    SSLContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);//源码内部默认取的是 2*n,  N=CPU数量 , 此处注意生产应该以压测结果为准。
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);//标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpHelloWorldServerInitializer(sslCtx));

            System.out.println("服务启动成功，访问地址：" + (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');
            Channel ch = b.bind(PORT).sync().channel();
            ch.closeFuture().sync();
            /* 单个netty是可以侦听多个端口的，一个端口一条线程，如果需要侦听多个端口，如下所示：
            List<Integer> ports = Arrays.asList(8080, 8081);
            Collection<Channel> channels = new ArrayList<>(ports.size());
            for (int port : ports) {
                Channel serverChannel = b.bind(port).sync().channel();
                channels.add(serverChannel);
            }
            for (Channel ch : channels) {
                ch.closeFuture().sync();
            } */
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}