package com.kakaoscan.profile.domain.respon.exception;

import com.kakaoscan.profile.domain.respon.enums.ApiErrorCase;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiException extends RuntimeException {
    private final ApiErrorCase errorCase;
}
