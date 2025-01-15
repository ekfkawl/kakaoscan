package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.application.service.UserService;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.model.oauth2.NaverOAuth2User;
import com.kakaoscan.server.domain.user.model.oauth2.OAuth2UserNaverClient;
import com.kakaoscan.server.domain.user.model.oauth2.OAuth2UserNaverClientToken;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.ekfkawl.ExceptionSupportUtils.handleException;

@Service
@RequiredArgsConstructor
public class NaverUserDetailsService {
    private final OAuth2UserNaverClientToken OAuth2UserNaverClientToken;
    private final OAuth2UserNaverClient OAuth2UserNaverClient;
    private final UserService userService;

    private static final String CLIENT_ID = System.getenv("NAVER_OAUTH_CLIENT_ID");
    private static final String SECRET_ID = System.getenv("NAVER_OAUTH_SECRET_ID");

    @SuppressWarnings("unchecked")
    public NaverOAuth2User loadUserByCode(String code, String state) {
        try {
            Map<String, Object> accessToken = OAuth2UserNaverClientToken.getAccessToken("authorization_code", CLIENT_ID, SECRET_ID, code, state);
            Map<String, Object> userInfo = OAuth2UserNaverClient.getUserInfo("Bearer " + accessToken.get("access_token"));
            Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
            response.computeIfPresent("id", (k, v) -> v + "@kakaoscan.com");

            String id = (String) response.get("id");
            User user = userService.findOrRegisterOAuthUser(id, AuthenticationType.NAVER);

            return new NaverOAuth2User(response, user.getAuthorities());

        } catch (FeignException e) {
            return handleException("failed to retrieve user info from Naver", e, SecurityException.class);
        }
    }
}
