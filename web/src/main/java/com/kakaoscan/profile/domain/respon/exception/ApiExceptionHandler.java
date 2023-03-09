package com.kakaoscan.profile.domain.respon.exception;

import com.kakaoscan.profile.domain.dto.ApiErrorDTO;
import com.kakaoscan.profile.domain.respon.enums.ApiErrorCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static com.kakaoscan.profile.domain.enums.MessageSendType.USER_NO_PERMISSION;

/**
 * 예외 처리 핸들러
 */
@RestControllerAdvice("com.kakaoscan.profile.domain")
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity handleApiException(final ApiException e) {
        return ResponseEntity
                .status(e.getErrorCase().getStatus())
                .body(ApiErrorDTO.builder()
                        .status(e.getErrorCase().getStatus())
                        .message(e.getErrorCase().getMessage())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity handleConstraintViolationException() {
        return ResponseEntity
                .status(ApiErrorCase.INVALID_PARAMETER.getStatus())
                .body(ApiErrorDTO.builder()
                        .status(ApiErrorCase.INVALID_PARAMETER.getStatus())
                        .message("올바른 데이터 형식이 아닙니다")
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            String errorMessage = fieldError.getDefaultMessage();
            stringBuilder.append(errorMessage).append("\n");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(stringBuilder.toString())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiErrorDTO> handleAccessDeniedExceptionException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(ApiErrorDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(USER_NO_PERMISSION.getType())
                        .build());
    }
}
