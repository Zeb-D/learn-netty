package com.yd.websocket.rm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.TimeUnit;

public class Client {

    public static WebSocketClient client;

    private String requestId;

    //本地测试 记得改下 服务器Ip
    public static void main(String[] args)
            throws URISyntaxException, NotYetConnectedException, IOException, InterruptedException {
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        Client client = new Client();
                        try {
                            client.connect(i);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            }).start();
            ;
        }
    }

    public static void send(byte[] bytes) {
        client.send(bytes);
    }

    public void connect(int i) throws InterruptedException {
        try {
            //61.164.213.244   xixiqueen.club
            client = new WebSocketClient(new URI("ws://192.168.2.47:7120/"), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake arg0) {
                    System.out.println("打开链接");
                }

                @Override
                public void onMessage(String arg0) {
                    System.out.println("服务器推送消息" + arg0);
                    JSONObject jsonObject = JSON.parseObject(arg0);
                    int serviceId = jsonObject.getInteger("serviceId");
                    if (serviceId == 1001)
                        requestId = jsonObject.getString("requestId");
                }

                @Override
                public void onError(Exception arg0) {
                    arg0.printStackTrace();
                    System.out.println("发生错误已关闭");
                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2) {
                    System.out.println("连接已关闭");
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    try {
                        String reback = new String(bytes.array(), "utf-8");
                        System.out.println(reback);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            };
            client.connect();
            while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                System.out.print("...");
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("serviceId", 1001);
            jsonObject.put("roomId", "421");
            String name = "USER-CLIENT-" + i;
            jsonObject.put("name", name);
            client.send(jsonObject.toString());
            System.out.println("已连接至服务器");
            //TimeUnit.MILLISECONDS.sleep(500);

            //发送消息
            /*obj.addProperty("requestId",requestId);
            obj.addProperty("serviceId", 1002);
			obj.addProperty("name", name);
			obj.addProperty("message", name + "发的测试消息");
			client.send(obj.toString());
			TimeUnit.MILLISECONDS.sleep(800);*/
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("连接服务器失败");
        }
    }

    //发送消息
    public void sendMsg() {
        try {
            TimeUnit.SECONDS.sleep(1);
            JSONObject obj = new JSONObject();
            obj.put("requestId", 961835648837357568l);
            obj.put("serviceId", 1002);
            obj.put("name", "test-01");
            obj.put("message", "测试消息");
            client.send(obj.toString());
        } catch (NotYetConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //下线
    public void offLine() {
        try {
            TimeUnit.SECONDS.sleep(1);
            JSONObject obj = new JSONObject();
            // {"requestId":uuid, "serviceId":1001, "name":name}
            client.close();
        } catch (NotYetConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
