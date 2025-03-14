package com.ruoyi.uni.util;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    // 设置当前请求
    public static void setRequest(HttpServletRequest request) {
        requestHolder.set(request);
    }

    // 获取当前请求
    public static HttpServletRequest getRequest() {
        return requestHolder.get();
    }

    // 清空当前请求
    public static void clear() {
        requestHolder.remove();
    }
}