package com.kakaoscan.server.domain.user.model.oauth2;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "naverUserToken", url = "https://nid.naver.com")
public interface OAuth2UserNaverClientToken {
    @GetMapping("/oauth2.0/token")
    Map<String, Object> getAccessToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code,
            @RequestParam("state") String state
    );
}
