package com.kakaoscan.profile.domain.bridge;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class ClientQueue {
    /**
     * 요청 시간순으로 정렬
     */
    private long requestTick;
    /**
     * 마지막 수신 tick
     */
    private long lastReceivedTick;
    /**
     * 마지막 send tick
     */
    private long lastSendTick;
    /**
     * 요청 전화번호
     */
    private String request;
    /**
     * view 응답
     */
    private String response;
    /**
     * connect flag
     */
    private boolean connected;
    /**
     * response connected state
     */
    private boolean fail;
}
