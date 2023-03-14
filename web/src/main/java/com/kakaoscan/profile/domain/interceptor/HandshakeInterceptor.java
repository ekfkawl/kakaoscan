package com.kakaoscan.profile.domain.interceptor;

import com.kakaoscan.profile.domain.dto.UserDTO;
import com.kakaoscan.profile.global.session.instance.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.kakaoscan.profile.global.session.instance.SessionManager.SESSION_FORMAT;
import static com.kakaoscan.profile.global.session.instance.SessionManager.SESSION_KEY;
import static com.kakaoscan.profile.utils.GenerateUtils.StrToMD5;
import static com.kakaoscan.profile.utils.HttpRequestUtils.getRemoteAddress;

@Configuration
@RequiredArgsConstructor
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final SessionManager sessionManager;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        HttpServletRequest httpRequest = serverRequest.getServletRequest();

        String remoteAddress = StrToMD5(getRemoteAddress(httpRequest), "");
        attributes.put("remoteAddress", remoteAddress);

        Optional<Cookie> optionalCookie = Arrays.stream(((ServletServerHttpRequest) request).getServletRequest().getCookies())
                .filter(cookie -> SESSION_KEY.equals(cookie.getName()))
                .findFirst();

        optionalCookie.ifPresent(cookie -> {
            Object userObj = sessionManager.getValue(String.format(SESSION_FORMAT, cookie.getValue()));
            if (userObj instanceof UserDTO) {
                UserDTO user = (UserDTO) userObj;
                attributes.put("user", user);
            }
        });

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}