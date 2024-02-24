package com.kakaoscan.server.domain.user.model.oauth2;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "googleUser", url = "https://www.googleapis.com")
public interface OAuth2UserGoogleClient {
    @GetMapping("/oauth2/v3/userinfo")
    Map<String, Object> getUserInfo(@RequestHeader("Authorization") String accessToken);
}
