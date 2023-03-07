package com.kakaoscan.profile.domain.interceptor;

import com.kakaoscan.profile.global.oauth.OAuthAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.kakaoscan.profile.utils.GenerateUtils.StrToMD5;
import static com.kakaoscan.profile.utils.HttpRequestUtils.getRemoteAddress;

/**
 * HttpSession의 정보를 WebSocketSession에 전달
 */
@Configuration
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        HttpServletRequest httpRequest = serverRequest.getServletRequest();

        String remoteAddress = StrToMD5(getRemoteAddress(httpRequest), "");
        attributes.put("remoteAddress", remoteAddress);

        HttpSession httpSession = httpRequest.getSession();
        Object userObj = httpSession.getAttribute("user");
        if (userObj instanceof OAuthAttributes) {
            OAuthAttributes user = (OAuthAttributes) userObj;
            attributes.put("user", user);
        }

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}