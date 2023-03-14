package com.kakaoscan.profile.domain.controller.api;

import com.kakaoscan.profile.domain.client.NettyClientInstance;
import com.kakaoscan.profile.domain.enums.ApiErrorCase;
import com.kakaoscan.profile.domain.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NettyStateController extends ApiBaseController {
    private final NettyClientInstance nettyClientInstance;

    /**
     * 서버 접속 상태 요청
     */
    @PostMapping("/status")
    public void status() {
        if (!nettyClientInstance.isConnected()) {
            throw new ApiException(ApiErrorCase.SERVER_ERROR);
        }
    }

}
