package com.kakaoscan.profile.domain.respon.exception;

import com.kakaoscan.profile.domain.dto.ApiErrorDTO;
import com.kakaoscan.profile.domain.respon.enums.ApiErrorCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * 예외 처리 핸들러
 */
@RestControllerAdvice("com.kakaoscan.profile.domain")
public class ApiExceptionHandler {

    @ExceptionHandler({ApiException.class})
    protected ResponseEntity apiException(final ApiException e) {
        return ResponseEntity
                .status(e.getErrorCase().getStatus())
                .body(ApiErrorDTO.builder()
                        .status(e.getErrorCase().getStatus())
                        .message(e.getErrorCase().getMessage())
                        .build());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity constraintViolationException() {
        return ResponseEntity
                .status(ApiErrorCase.INVALID_PARAMETER.getStatus())
                .body(ApiErrorDTO.builder()
                        .status(ApiErrorCase.INVALID_PARAMETER.getStatus())
                        .message("올바른 데이터 형식이 아닙니다")
                        .build());
    }
}
