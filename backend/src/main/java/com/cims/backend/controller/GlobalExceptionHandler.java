package com.cims.backend.controller;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 全局异常处理器，统一返回标准错误响应
 */

import com.cims.backend.dto.ApiErrorCode;
import com.cims.backend.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

/**
 * 统一异常到 {@link ApiResponse} 与 HTTP 状态；非预期异常记 error 并避免泄露内部细节给客户端。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String DEFAULT_VALIDATION_MESSAGE = "Invalid request";
    private static final String DEFAULT_FORBIDDEN_MESSAGE = "No permission";
    private static final String DEFAULT_INTERNAL_ERROR_MESSAGE = "Internal server error";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request / auth: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.fail(ApiErrorCode.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        String message = resolveValidationMessage(ex);
        return fail(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST, message);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return fail(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return fail(HttpStatus.FORBIDDEN, ApiErrorCode.FORBIDDEN, DEFAULT_FORBIDDEN_MESSAGE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOtherException(Exception ex) {
        log.error("Unhandled exception", ex);
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_ERROR, DEFAULT_INTERNAL_ERROR_MESSAGE);
    }

    private ResponseEntity<ApiResponse<Void>> fail(HttpStatus status, int code, String message) {
        return ResponseEntity.status(status).body(ApiResponse.fail(code, message));
    }

    private String resolveValidationMessage(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null || fieldError.getDefaultMessage() == null || fieldError.getDefaultMessage().trim().isEmpty()) {
            return DEFAULT_VALIDATION_MESSAGE;
        }
        return fieldError.getDefaultMessage();
    }
}
