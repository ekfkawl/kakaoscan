package com.kakaoscan.profile.global.security.handler;

import com.kakaoscan.profile.domain.service.UserRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.kakaoscan.profile.utils.GenerateUtils.StrToMD5;
import static com.kakaoscan.profile.utils.HttpRequestUtils.getRemoteAddress;

@Log4j2
@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final UserRequestService userRequestService;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String remoteAddress = StrToMD5(getRemoteAddress(request), "");

        // 오늘 사용 데이터가 없으면 0으로 초기화
        if (userRequestService.getTodayUseCount(email) == -1) {
            userRequestService.initUseCount(email, remoteAddress);
        }

        setDefaultTargetUrl("/");

        redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
    }
}

