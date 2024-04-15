package com.kakaoscan.server.infrastructure.email.types;

import com.kakaoscan.server.infrastructure.email.template.EmailTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationEmail extends EmailTemplate {
    private String verificationLink;

    public VerificationEmail(String receiver, String verificationLink) {
        super(receiver, "[카카오스캔] 가입 인증 메일입니다.", "verification_email");
        this.verificationLink = verificationLink;
    }
}
