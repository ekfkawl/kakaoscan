package com.kakaoscan.server.application.dto;

import com.kakaoscan.server.common.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "이메일에 공백이 포함될 수 없습니다.")
    @Pattern(regexp = ValidationPatterns.EMAIL_AND_DOMAIN, message = "올바른 이메일 형식이 아니거나 허용되지 않은 도메인입니다.")
    private String email;

    @NotBlank(message = "비밀번호에 공백이 포함될 수 없습니다.")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자만 사용해 주세요.")
    private String password;

    public static int getFieldPriority(FieldError error) {
        return switch (error.getField()) {
            case "email" -> 1;
            case "password" -> 2;
            default -> Integer.MAX_VALUE;
        };
    }
}
