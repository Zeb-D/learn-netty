package com.yd.websocket.rm.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private String requestId;
    private int serviceId;
    private boolean isSucc;
    private String name;
    private String message;
    private Map<String, String> hadOnline = new HashMap<String, String>(); // <requestId, name>

    public static Response create(String json) {
        if (!StringUtil.isNullOrEmpty(json)) {
            return JSON.parseObject(json, Response.class);
        }
        return null;
    }

    public String getRequestId() {
        return requestId;
    }

    public Response setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public int getServiceId() {
        return serviceId;
    }

    public Response setServiceId(int serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public boolean getIsSucc() {
        return isSucc;
    }

    public Response setIsSucc(boolean isSucc) {
        this.isSucc = isSucc;
        return this;
    }

    public String getName() {
        return name;
    }

    public Response setName(String name) {
        this.name = name;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Map<String, String> getHadOnline() {
        return hadOnline;
    }

    public Response setHadOnline(Map<String, String> hadOnline) {
        this.hadOnline = hadOnline;
        return this;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

}
