package com.yd.websocket.rm.util;

import com.alibaba.fastjson.JSON;

public class Request {

    private String requestId;
    private int serviceId;
    private String name;
    private String message;
    private String time;
    private int type;  //消息类型：1-欢迎  2-普通消息 3-本人
    private String roomId;  //房间id

    public Request(String requestId, int serviceId, String name) {
        this.requestId = requestId;
        this.serviceId = serviceId;
        this.name = name;
    }

    public Request(String requestId, int serviceId, String name, String message, String time) {
        this.requestId = requestId;
        this.serviceId = serviceId;
        this.name = name;
        this.message = message;
        this.time = time;
    }

    public Request(int serviceId, String name, String message, String time) {
        this.serviceId = serviceId;
        this.name = name;
        this.message = message;
        this.time = time;
    }

    public Request() {
    }

    public static Request create(String json) {
        if (!StringUtil.isNullOrEmpty(json)) {
            return JSON.parseObject(json, Request.class);
        }
        return null;
    }

    public String getRequestId() {
        return requestId;
    }

    public Request setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public int getServiceId() {
        return serviceId;
    }

    public Request setServiceId(int serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Request setName(String name) {
        this.name = name;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Request setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

}
