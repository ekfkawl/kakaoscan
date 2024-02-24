package com.kakaoscan.server.application.controller.exception;

import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.exception.EmailNotVerifiedException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<String> messages = fieldErrors.stream()
                .sorted(Comparator.comparingInt(RegisterRequest::getFieldPriority))
                .map(FieldError::getDefaultMessage)
                .toList();

        String message = messages.isEmpty() ? "validation error" : messages.get(0);
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleUsernameNotFoundException() {
        return ResponseEntity.ok(new ApiResponse(false, "아이디 또는 비밀번호 오류입니다."));
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse> handleEmailNotVerifiedException() {
        return ResponseEntity.ok(new ApiResponse(false, "이메일 인증을 완료해 주세요."));
    }
}
