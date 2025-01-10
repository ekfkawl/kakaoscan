package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.application.service.UserService;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.model.oauth2.GoogleOAuth2User;
import com.kakaoscan.server.domain.user.model.oauth2.OAuth2UserGoogleClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.kakaoscan.server.common.utils.ExceptionHandler.handleException;

@Service
@RequiredArgsConstructor
public class GoogleUserDetailsService {
    private final OAuth2UserGoogleClient OAuth2UserGoogleClient;
    private final UserService userService;

    public GoogleOAuth2User loadUserByAccessToken(String accessToken) {
        try {
            Map<String, Object> userInfo = OAuth2UserGoogleClient.getUserInfo("Bearer " + accessToken);

            String email = (String) userInfo.get("email");
            User user = userService.findOrRegisterOAuthUser(email, AuthenticationType.GOOGLE);

            return new GoogleOAuth2User(userInfo, user.getAuthorities());

        } catch (FeignException e) {
            handleException("failed to retrieve user info from Google", e, SecurityException.class);
            return null;
        }
    }
}
