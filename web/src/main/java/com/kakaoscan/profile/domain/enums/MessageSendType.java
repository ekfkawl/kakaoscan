package com.kakaoscan.profile.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageSendType {
    // WindowServer
    PROFILE("Profile"),
    HEARTBEAT("Heartbeat"),

    // Client
    REMAINING_QUEUE("{\"RemainingQueue\":\"대기 인원: %d명 (%d초 남음)\"}"),
    TURN_LOCAL("{\"RemainingQueue\":\"프로필을 조회 중이에요!\"}"),
    ACCESS_LIMIT("{\"Error\":\"일일 사용량이 초과됐습니다\\n내일 다시 시도해주세요:)\"}"),
    LOCAL_ACCESS_LIMIT("{\"Error\":\"신규 번호는 계정/IP 당 일일 최대 %d번 조회 가능합니다\\n내일 다시 시도해주세요:)\"}"),
    CONNECT_CLOSE_IP("{\"Error\":\"다른 세션에서 접속이 감지되었거나 연결 세션이 만료되었습니다\"}"),
    SERVER_INSTANCE_NOT_RUN("{\"Error\":\"서버가 실행 중이 아닙니다\"}"),
    USER_NOT_FOUND("{\"Error\":\"로그인이 필요한 서비스입니다\"}"),
    USER_NO_PERMISSION("{\"Error\":\"해당 계정은 서비스 사용 권한이 없습니다\"}"),
    REQUEST_TIME_OUT("{\"Error\":\"요청 시간이 초과되었습니다\"}"),
    EMPTY_IP("{\"Error\":\"잘못된 접근입니다\"}");

    private final String message;
}
