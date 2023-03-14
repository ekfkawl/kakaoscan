package com.kakaoscan.profile.global.mvc.handler;

import com.kakaoscan.profile.global.session.instance.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;

import static com.kakaoscan.profile.global.session.instance.SessionManager.SESSION_FORMAT;
import static com.kakaoscan.profile.utils.HttpRequestUtils.getCookie;

@Component
@RequiredArgsConstructor
public class SecurityContextHandlerInterceptor implements HandlerInterceptor {

    private final SessionManager sessionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // security context 에 인증 정보가 없는데 세션에 존재하면 로그아웃 처리
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        boolean isAnonymousUser = authorities.stream().anyMatch(auth -> "ROLE_ANONYMOUS".equals(auth.getAuthority()));

        if (isAnonymousUser) {
            Optional<Cookie> optionalCookie = getCookie(request);
            if (optionalCookie.isPresent()) {
                Object userObj = sessionManager.getValue(String.format(SESSION_FORMAT, optionalCookie.get().getValue()));
                if (userObj != null) {
                    sessionManager.deleteValue(String.format(SESSION_FORMAT, optionalCookie.get().getValue()));
                }
            }
        }

        return true;
    }
}