package com.kakaoscan.server.application.controller.exception;

import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.exception.DeletedUserException;
import com.kakaoscan.server.application.exception.EmailNotVerifiedException;
import com.kakaoscan.server.application.exception.PendingTransactionExistsException;
import com.kakaoscan.server.application.exception.TransactionIllegalStateException;
import com.kakaoscan.server.infrastructure.exception.DataNotFoundException;
import com.kakaoscan.server.infrastructure.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Comparator;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException e) {
        return new ResponseEntity<>(ApiResponse.failure(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<String> messages = fieldErrors.stream()
                .sorted(Comparator.comparingInt(RegisterRequest::getFieldPriority))
                .map(FieldError::getDefaultMessage)
                .toList();

        String message = messages.isEmpty() ? "validation error" : messages.get(0);
        return new ResponseEntity<>(ApiResponse.failure(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException() {
        return new ResponseEntity<>(ApiResponse.failure("존재하지 않는 아이디입니다."), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException() {
        return new ResponseEntity<>(ApiResponse.failure("비밀번호 오류입니다."), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailNotVerifiedException() {
        return new ResponseEntity<>(ApiResponse.failure("이메일 인증을 완료해 주세요."), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DeletedUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleDeletedUserException() {
        return new ResponseEntity<>(ApiResponse.failure("탈퇴 처리된 계정입니다."), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PendingTransactionExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handlePendingTransactionExistsException(PendingTransactionExistsException e) {
        return new ResponseEntity<>(ApiResponse.failure(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataNotFoundException(DataNotFoundException e) {
        return new ResponseEntity<>(ApiResponse.failure(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionIllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleTransactionIllegalStateException(TransactionIllegalStateException e) {
        return new ResponseEntity<>(ApiResponse.failure(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        return new ResponseEntity<>(ApiResponse.failure(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(ApiResponse.failure(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
