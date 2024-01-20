package com.kakaoscan.server.infrastructure.constants;

public class ResponseMessages {
    public static final String SEARCH_QUEUE_WAITING = "대기 인원: %d명";
    public static final String SEARCH_WAITING = "서버에 연결 중 입니다..";
    public static final String SEARCH_STARTING = "프로필 조회를 시작합니다..";
    public static final String SEARCH_ERROR_PING_PONG = "서버 연결에 실패하였습니다.";
    public static final String SEARCH_INVALID_PHONE_NUMBER = "입력하신 번호를 조회할 수 없습니다.";
    public static final String SEARCH_TOO_MANY_INVALID_PHONE_NUMBER = "죄송합니다, 조회가 불가능한 전화번호의 요청이 과도하게 감지되어 최대 2시간 동안 사용이 제한됩니다.";
}
