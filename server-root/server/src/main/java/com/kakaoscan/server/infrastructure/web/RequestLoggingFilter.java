package com.kakaoscan.server.infrastructure.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            String requestId = UUID.randomUUID().toString();
            ThreadContext.put("requestId", requestId);

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            ThreadContext.clearAll();
        }
    }
}