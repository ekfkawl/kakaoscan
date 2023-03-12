package com.kakaoscan.profile.global.security.handler;

import com.kakaoscan.profile.domain.service.UserRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

import static com.kakaoscan.profile.utils.GenerateUtils.StrToMD5;
import static com.kakaoscan.profile.utils.HttpRequestUtils.getRemoteAddress;

@Log4j2
@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final UserRequestService userRequestService;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        // 계정 별 사용 횟수 동기화
        String remoteAddress = StrToMD5(getRemoteAddress(request), "");
        userRequestService.syncUserUseCount(remoteAddress, LocalDate.now());
        log.info("login client remote address : {}, {}", getRemoteAddress(request), remoteAddress);

        setDefaultTargetUrl("/");

        redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
    }
}

