package com.kakaoscan.server.application.dto;

import lombok.Getter;

@Getter
public class VerificationEmail extends EmailTemplate {
    private final String verificationLink;

    public VerificationEmail(String receiver, String verificationLink) {
        super(receiver, "카카오스캔 가입 인증 메일입니다.", "verification_email");
        this.verificationLink = verificationLink;
    }
}
