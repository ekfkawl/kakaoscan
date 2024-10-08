package com.kakaoscan.server.infrastructure.constants;

public class ResponseMessages {
    public static final String SEARCH_QUEUE_WAITING = "대기 인원: %d명";
    public static final String SEARCH_WAITING = "서버에 연결 중 입니다..";
    public static final String SEARCH_STARTING = "프로필 조회를 시작합니다..";
    public static final String SEARCH_ERROR_PING_PONG = "서버 연결에 실패하였습니다. 잠시 후 재시도 해주세요.";
    public static final String SEARCH_INVALID_PHONE_NUMBER = "입력하신 번호를 조회할 수 없습니다.";
    public static final String SEARCH_INVALID_KAKAO_ID = "입력하신 아이디를 조회할 수 없습니다.";
    public static final String SEARCH_INVALID_PHONE_NUMBER_EX = "입력하신 번호를 조회할 수 없습니다, 상대방의 '전화번호로 친구 추가 허용' 옵션이 비활성화 상태면 조회가 불가능합니다.";
    public static final String SEARCH_NOT_PHONE_NUMBER_FORMAT = "올바른 번호 형식이 아닙니다.";
    public static final String SEARCH_NOT_KAKAO_ID_FORMAT = "올바른 아이디 형식이 아닙니다.";
    public static final String SEARCH_CONTINUE = "이전의 작업을 이어서 진행합니다.. %s";
    public static final String SEARCH_TOO_MANY_INVALID_PHONE_NUMBER = "죄송합니다, 조회가 불가능한 전화번호의 요청이 과도하게 감지되어 최대 2시간 동안 사용이 제한됩니다.";
    public static final String NOT_ENOUGH_POINTS = "%s 포인트가 부족합니다.";
    public static final String MAX_DAILY_NEW_NUMBER_SEARCH = "죄송합니다, 오늘의 신규 번호 조회 가능 횟수가 모두 소진되었습니다. 내일 다시 확인해주세요.";
    public static final String CONCURRENT_MODIFICATION_POINTS = "잠시 후 다시 시도해 주세요.";

    public static final String LOADING_POINTS_BALANCE = "로딩 중..";
}
