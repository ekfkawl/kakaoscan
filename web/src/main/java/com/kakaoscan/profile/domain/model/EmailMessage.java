package com.kakaoscan.profile.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailMessage {
    /**
     * 수신자
     */
    private String to;
    /**
     * 메일 제목
     */
    private String subject;
    /**
     * 메일 내용
     */
    private String message;
}