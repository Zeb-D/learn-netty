package com.yd.websocket.rm.service;

import com.yd.websocket.rm.util.Request;

import io.netty.channel.ChannelHandlerContext;

public interface SendCallBack {
	
	// 服务端发送消息给客户端
	void send(Request request) throws Exception;
	
	//获取当前channel
	ChannelHandlerContext getChannelCxt();
	
}
