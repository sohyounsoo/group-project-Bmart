package com.group.Bmart.global.advice;


import com.group.Bmart.global.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleException(final Exception exception) {
        log.error(exception.getMessage(), exception);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR") // 예외 코드
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // HTTP 상태 코드
                .message(exception.getMessage()) // 예외 메시지
                .build();

        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getDefaultMessage()).append("\n");
        }
        log.error(errorMessage.toString(), exception);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR_CODE") // 예외 코드
                .status(HttpStatus.BAD_REQUEST.value()) // HTTP 상태 코드
                .message(errorMessage.toString()) // 예외 메시지
                .build();

        return createErrorResponse(HttpStatus.BAD_REQUEST, errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<ErrorResponse> handleRequestParameterException(MissingServletRequestParameterException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("MISSING_PARAMETER_ERROR_CODE") // 예외 코드
                .status(HttpStatus.BAD_REQUEST.value()) // HTTP 상태 코드
                .message("요청 파라미터가 누락되었습니다.") // 예외 메시지
                .build();

        return createErrorResponse(HttpStatus.BAD_REQUEST, errorResponse);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, ErrorResponse errorResponse) {
        return ResponseEntity.status(status).body(errorResponse);
    }
}
