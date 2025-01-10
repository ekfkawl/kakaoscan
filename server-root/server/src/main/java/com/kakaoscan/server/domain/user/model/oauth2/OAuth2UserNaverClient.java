package com.kakaoscan.server.domain.user.model.oauth2;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "naverUser", url = "https://openapi.naver.com")
public interface OAuth2UserNaverClient {
    @PostMapping("/v1/nid/me")
    Map<String, Object> getUserInfo(
            @RequestHeader("Authorization") String accessToken
    );
}
