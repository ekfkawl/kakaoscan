package com.kakaoscan.profile.global.security.handler;

import com.kakaoscan.profile.global.session.instance.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.kakaoscan.profile.global.session.instance.SessionManager.SESSION_FORMAT;
import static com.kakaoscan.profile.utils.HttpRequestUtils.getCookie;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final SessionManager sessionManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        getCookie(request)
                .ifPresent(cookie -> sessionManager.deleteValue(String.format(SESSION_FORMAT, cookie.getValue())));
    }
}
