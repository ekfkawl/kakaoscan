package com.kakaoscan.profile.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorDTO {
    /**
     * 예외 코드
     */
    private int status;
    /**
     * 예외 메세지
     */
    private String message;
}
