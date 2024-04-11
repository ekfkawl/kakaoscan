package com.kakaoscan.server.application.dto.request;

import com.kakaoscan.server.common.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "비밀번호에 공백이 포함될 수 없습니다.")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자만 사용해 주세요.")
    private String password;
}
