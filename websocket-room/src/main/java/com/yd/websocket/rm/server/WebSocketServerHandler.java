package com.yd.websocket.rm.server;

import com.yd.websocket.rm.serviceimpl.SendService;
import com.rm.util.*;
import com.yd.websocket.rm.util.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * WebSocket服务端Handler
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServerHandler.class.getName());

    private WebSocketServerHandshaker handshaker;
    private ChannelHandlerContext ctx;
    private String sessionId;
    private String roomId;

    /**
     * Http返回
     *
     * @param ctx
     * @param request
     * @param response
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        // 返回应答给客户端
        if (response.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(response, response.content().readableBytes());
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(response);
        if (!HttpHeaders.isKeepAlive(request) || response.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) { // 传统的HTTP接入
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) { // WebSocket接入
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("WebSocket异常", cause);
        ctx.close();
        SendService.counter.decrementAndGet();
        LOG.info(sessionId + " 	注销");
    }

    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        LOG.info("WebSocket关闭");
        super.close(ctx, promise);
        SendService.counter.decrementAndGet();
        LOG.info(sessionId + " 注销");
    }

    /**
     * 处理Http请求，完成WebSocket握手<br/>
     * 注意：WebSocket连接第一次请求使用的是Http
     *
     * @param ctx
     * @param request
     * @throws Exception
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // 正常WebSocket的Http连接请求，构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://" + request.headers().get(HttpHeaders.Names.HOST), null, false);
        handshaker = wsFactory.newHandshaker(request);
        LOG.info("处理Http请求:" + handshaker.uri() + handshaker.version());
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else { // 向客户端发送websocket握手,完成握手
            handshaker.handshake(ctx.channel(), request);
            this.ctx = ctx;
        }
    }

    /**
     * 处理Socket请求
     *
     * @param ctx
     * @param frame
     * @throws Exception
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("当前只支持文本消息，不支持二进制消息");
        }

        // 处理来自客户端的WebSocket请求
        try {
            Request request = Request.create(((TextWebSocketFrame) frame).text());
            Response response = new Response();
            response.setServiceId(request.getServiceId());

            String requestId = request.getRequestId();
            String roomId = "";
            if (CODE.online.code.intValue() == request.getServiceId()) { // 客户端注册
                if (StringUtil.isNullOrEmpty(request.getRoomId())) {
                    response.setIsSucc(false).setMessage("roomId不能为空");
                    sendWebSocket(response.toJson());
                    return;
                }
                roomId = request.getRoomId();
                if (StringUtil.isNullOrEmpty(request.getName())) {
                    response.setIsSucc(false).setMessage("name不能为空");
                    sendWebSocket(response.toJson());
                    return;
                }
                StringBuffer sb = new StringBuffer();
                if (!SendService.register(roomId, sb, new SendService(ctx, request.getName()))) {
                    response.setIsSucc(false).setMessage("注册失败");
                    sendWebSocket(response.toJson());
                } else {
                    requestId = sb.toString();
                    response.setIsSucc(true).setRequestId(requestId + "_" + roomId);
                    request.setRequestId(requestId + "_" + roomId);
                    //.setMessage("注册成功");
                    LOG.info("客户端[" + request.getName() + "]上线");
                    nolifyAllClient(request, CODE.online.code);

                }
                sendWebSocket(response.toJson());
                this.sessionId = requestId; // 记录会话id，当页面刷新或浏览器关闭时，注销掉此链路
                this.roomId = roomId;
            } else if (CODE.send_message.code.intValue() == request.getServiceId()) { // 客户端发送消息到聊天群
                if (StringUtil.isNullOrEmpty(requestId)) {
                    response.setIsSucc(false).setMessage("requestId不能为空");
                } else if (StringUtil.isNullOrEmpty(request.getName())) {
                    response.setIsSucc(false).setMessage("name不能为空");
                } else if (StringUtil.isNullOrEmpty(request.getMessage())) {
                    response.setIsSucc(false).setMessage("message不能为空");
                } else {
                    response.setIsSucc(true);
                    if ("GCS".equals(request.getMessage())) {
                        SendService.getRoomInfo();
                    }
                    //.setMessage("发送消息成功");
                    nolifyAllClient(request, CODE.receive_message.code);
                }
                sendWebSocket(response.toJson());
            } else if (CODE.downline.code.intValue() == request.getServiceId()) { // 客户端下线
                if (StringUtil.isNullOrEmpty(requestId)) {
                    sendWebSocket(response.setIsSucc(false).setMessage("requestId不能为空").toJson());
                }
                /*else {
                    SendService.logout(requestId);
					response.setIsSucc(true);
					//.setMessage("下线成功");

					SendService.notifyDownline(requestId); // 通知有人下线
					LOG.info("客户端-"+request.getName()+"已下线");
					LOG.info("当前服务器客户端数量为："+SendService.SendWatchMap.size());
					sendWebSocket(response.toJson());
				}*/

            } else {
                sendWebSocket(response.setIsSucc(false).setMessage("未知请求").toJson());
            }
        } catch (Exception e2) {
            LOG.error("处理Socket请求异常", e2);
        }
    }

    //通知所有客户端
    public void nolifyAllClient(Request request, int code) {
        String time = DateUtil.getCurrentDateStr("HH:mm:ss:sss");
        String[] strs = request.getRequestId().split("_");
        String roomId = strs[1];
        code = request.getServiceId();
        ChannelGroup groups = SendService.SendWatchMap.get(roomId);  //成员广播组
        Request serviceRequest = new Request(CODE.receive_message.code, request.getName(), request.getMessage(), time);
        try {
            if (CODE.online.code.equals(code)) { //上线
                serviceRequest.setType(1);
            } else {
                serviceRequest.setType(2);
            }
            LOG.info("服务器推送所有客户端信息：" + serviceRequest.toJson());
            TextWebSocketFrame tws = new TextWebSocketFrame(serviceRequest.toJson());
            groups.writeAndFlush(tws);
        } catch (Exception e) {
            LOG.warn("回调发送消息给客户端异常", e);
        }
    }

    /**
     * WebSocket返回
     *
     * @param msg
     */
    public void sendWebSocket(String msg) throws Exception {
        if (this.handshaker == null || this.ctx == null || this.ctx.isRemoved()) {
            throw new Exception("尚未握手成功，无法向客户端发送WebSocket消息");
        }
        this.ctx.channel().write(new TextWebSocketFrame(msg));
        this.ctx.flush();
    }
}
