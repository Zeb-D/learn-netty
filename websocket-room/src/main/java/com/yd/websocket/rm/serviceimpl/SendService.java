package com.yd.websocket.rm.serviceimpl;

import com.yd.websocket.rm.util.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.yd.websocket.rm.service.SendCallBack;
import com.yd.websocket.rm.util.Request;
import com.yd.websocket.rm.util.UuidGem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendService implements SendCallBack {
	private static final Logger LOG = LoggerFactory.getLogger(SendService.class);
	
	// <roomId, 房间成员组>
	public static final Map<String, ChannelGroup> SendWatchMap = new ConcurrentHashMap<String, ChannelGroup>(); 
	
	public static AtomicLong counter = new AtomicLong(0);
	
	private ChannelHandlerContext ctx;
	private String name;
	
	public SendService(ChannelHandlerContext ctx, String name) {
		this.ctx = ctx;
		this.name = name;
	}
	
	public ChannelHandlerContext getChannelCxt(){
		return this.ctx;
	}

	public static boolean register(String roomId, StringBuffer requestId, SendCallBack callBack) {
		if (StringUtil.isNullOrEmpty(roomId)) {
			return false;
		}
		String _requestId = requestId.toString();
		UuidGem ug = new UuidGem(1, 1); //这边默认workeId为1，datacenerid为1
		if(StringUtil.isNullOrEmpty(_requestId)){
			_requestId = String.valueOf(ug.nextId());
		}
		if(SendWatchMap.containsKey(roomId)){ //已存在该比赛的room
		}else{//初始化一个room
			ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
			SendWatchMap.put(roomId, channels);
		}
		SendWatchMap.get(roomId).add(callBack.getChannelCxt().channel());  //成员添加到广播组
		requestId.append(_requestId);
		LOG.info(String.format("当前房间%s人数为：%d", roomId, counter.incrementAndGet()));
		//getRoomInfo();
		return true;
	}
	
	public static void getRoomInfo(){
		int total = 0;
		LOG.info("服务器具体信息如下:---------------------------------------------------------");
		for(Entry<String, ChannelGroup> entry: SendWatchMap.entrySet()){
			LOG.info(String.format("id为 %s 的数量为 %d", entry.getKey(),entry.getValue().size()));
			total += entry.getValue().size();
		}
		LOG.info(String.format("当前服务器房间数为：%d,一共有%d人。以上..................................", SendWatchMap.size(),total));
	}
	
	@Override
	public void send(Request request) throws Exception {
		if (this.ctx == null || this.ctx.isRemoved()) {
			throw new Exception("尚未握手成功，无法向客户端发送WebSocket消息");
		}
		this.ctx.channel().write(new TextWebSocketFrame(request.toJson()));
		this.ctx.flush();
	}
	
	public String getName() {
		return name;
	}

}
