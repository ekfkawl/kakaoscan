package com.kakaoscan.profile.domain.respon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 예외 정의
 */
@AllArgsConstructor
@Getter
public enum ApiErrorCase {
    INVALID_PARAMETER(400, "값을 확인해주세요"),

    SERVER_ERROR(500, "서버 연결에 실패하였습니다");

    private final int status;
    private final String message;
}
