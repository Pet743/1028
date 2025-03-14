package com.ruoyi.uni.util;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化时不做任何操作
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        // 获取当前请求并设置到 RequestHolder 中
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        RequestHolder.setRequest(request);

        // 继续执行
        chain.doFilter(servletRequest, servletResponse);

        // 请求处理完毕后清理 ThreadLocal
        RequestHolder.clear();
    }

    @Override
    public void destroy() {
        // 销毁时不做任何操作
    }
}