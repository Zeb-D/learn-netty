package com.yd.netty.hello;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;


/**
 * @author Yd on 2018-06-29
 * @description
 */
public class BusinessThreadUtil {
    private static final ExecutorService executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000));//CPU核数4-10倍

    public static void doBusiness(ChannelHandlerContext ctx, Object msg, byte[] content) {
        //异步线程池处理
        executor.submit(() -> {
            if (msg instanceof HttpRequest) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread.currentThread().setName("buessness-thread");
                System.out.println(Thread.currentThread().getId());
                HttpRequest req = (HttpRequest) msg;
                boolean keepAlive = HttpHeaders.isKeepAlive(req);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
                response.headers().set("Content-Type", "text/plain");
                response.headers().set("Content-Length", response.content().readableBytes());
                if (!keepAlive) {
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set("Connection", "keep-alive");
                    ctx.writeAndFlush(response);
                }
            }
        });
    }
}
