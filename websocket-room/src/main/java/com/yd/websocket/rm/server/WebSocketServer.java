package com.yd.websocket.rm.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket服务
 */
public class WebSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    public WebSocketServer(int port) {
        try {
            run(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //java  -cp -Dnetty.server.parentgroup.size=2 -Dnetty.server.childgroup.size=4    -Dfile.encoding=UTF-8  WebSocketServer 7120
    public static void main(String[] args) {
//        PropertyConfigurator.configure(System.getProperty("conf.dir") + "/log4j.properties");
        if ((args == null) || (args.length == 0)) {
            new WebSocketServer(7120);
        } else if (args.length == 1) {
            new WebSocketServer(Integer.parseInt(args[0]));
        }
    }

    public void run(int port) throws Exception {
        LOG.info("WebSocket Server 启动 ....");
        LOG.info("WebSocket 尝试绑定端口：" + port + ".");

        //运行java -jar 加入以下参数： -Dnetty.server.parentgroup.size=2 -Dnetty.server.childgroup.size=4
        int parentThreadGroupSize = 2;
        if (null != Integer.getInteger("netty.server.parentgroup.size")) {
            parentThreadGroupSize = Integer.getInteger("netty.server.parentgroup.size");
        }
        int childThreadGroupSize = 4;
        if (null != Integer.getInteger("netty.server.childgroup.size")) {
            childThreadGroupSize = Integer.getInteger("netty.server.childgroup.size");
        }
        LOG.info(String.format("主线程组大小：%s 副线程组大小：%s", parentThreadGroupSize, childThreadGroupSize));

        EventLoopGroup bossGroup = new NioEventLoopGroup(parentThreadGroupSize);
        EventLoopGroup workerGroup = new NioEventLoopGroup(childThreadGroupSize);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, false)//是否启用心跳保活机制,在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
                    .option(ChannelOption.TCP_NODELAY, true)//TCP_NODELAY就是用于启用或关于Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
                    .option(ChannelOption.SO_BACKLOG, 1024)//BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("http-codec", new HttpServerCodec()); // Http消息编码解码
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536)); // Http消息组装
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler()); // WebSocket通信支持
                            pipeline.addLast("handler", new WebSocketServerHandler()); // WebSocket服务端Handler
                        }
                    });

            Channel channel = b.bind(port).sync().channel();
            LOG.info("WebSocket 已经启动，端口：" + port + ".");
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
