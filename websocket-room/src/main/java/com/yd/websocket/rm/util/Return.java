package com.yd.websocket.rm.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

/**
 * 通用返回对象封装
 *
 */
public class Return extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 5558814018967387112L;
	
	public enum Return_Fields {
		success, code, note
	}
	
	/************ Create ************/
	
	public static Return create() {
		return new Return();
	}
	
	public static Return create(String k, Object v) {
		return new Return().put(k, v);
	}
	
	/************ Success ************/
	public static Return SUCCESS(Integer code, String note) {
		Return pojo = new Return();
		pojo.put(Return_Fields.success.name(), true);
		pojo.put(Return_Fields.code.name(), code);
		pojo.put(Return_Fields.note.name(), note);
		return pojo;
	}
	
	public static Return SUCCESS(CODE code) {
		return SUCCESS(code.code, code.note);
	}
	
	/************ Fail ************/
	public static Return FAIL(Integer code, String note) {
		Return pojo = new Return();
		pojo.put(Return_Fields.success.name(), false);
		pojo.put(Return_Fields.code.name(), code);
		pojo.put(Return_Fields.note.name(), note);
		return pojo;
	}

	public static Return FAIL(CODE code) {
		return FAIL(code.code, code.note);
	}

	public static Return FAIL(CODE code, Exception e) {
		return FAIL(code.code, stacktrace(e));
	}
	
	/************ Getter Setter ************/
	public Boolean is_success() {
		Boolean value = (Boolean) this.get(Return_Fields.code.name());
		if("".equals(value)||null==value){
			value = false;
		}
		return value;
	}

	public Integer get_code() {
		Integer value = (Integer) this.get(Return_Fields.code.name());
		if("".equals(value)||null==value){
			value = CODE.error.code;
		}
		return value;
	}

	public String get_note() {
		String value = (String) this.get(Return_Fields.note.name());
		if("".equals(value)||null==value){
			value = "";
		}
		return value;
	}
	
	public String getOrDefault(String key){
		String value = (String) this.get(key);
		if("".equals(value)||null==value){
			value = "";
		}
		return value;
	}
	
	/************ Override ************/
	@Override
	public Return put(String k, Object v) {
		this.put(k, v);
		return this;
	}
	
	public Return add(String k, Object v) {
		this.put(k, v);
		return this;
	}
	
	public String toJson() {
		return JSON.toJSONString(this);
	}
	
	@Override
	public String toString() {
		return this.toJson();
	}
	
	/************ Tool ************/
	
	/**
	 * 打印异常
	 * @param e
	 * @return
	 */
	private static String stacktrace(Throwable e) {
		StringBuilder stack_trace = new StringBuilder();
		while (e != null) {
			String error_message = e.getMessage();
			error_message = error_message == null ? "\r\n" : error_message.concat("\r\n");
			stack_trace.append(error_message);
			stack_trace.append("<br>");
			for (StackTraceElement string : e.getStackTrace()) {
				stack_trace.append(string.toString());
				stack_trace.append("<br>");
			}
			e = e.getCause();
		}
		return stack_trace.toString();
	}
	
}
