package com.yd.netty.hello;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
public class HttpHelloWorldServerHandler extends ChannelInboundHandlerAdapter {

    private static final byte[] CONTENT = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("IO线程处理完毕：" + Thread.currentThread().getThreadGroup()+":"+Thread.currentThread().getName());
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws  Exception{
        BusinessThreadUtil.doBusiness(ctx, msg, CONTENT);//handle中，可以使用异步的线程池，处理业务。防止handler卡住，导致netty并发性能不佳
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(cause instanceof ReadTimeoutException||cause instanceof WriteTimeoutException) {
            System.out.println("超时了：" + cause.toString());
        }
        ctx.close();//直接关闭channel
    }
}