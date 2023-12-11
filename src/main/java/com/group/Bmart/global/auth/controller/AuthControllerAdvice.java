package com.group.Bmart.global.auth.controller;

import com.group.Bmart.global.auth.exception.*;
import com.group.Bmart.global.util.ErrorTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthControllerAdvice {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorTemplate> authExHandle(AuthException ex) {
        return ResponseEntity.badRequest()
            .body(ErrorTemplate.of(ex.getMessage()));
    }

    @ExceptionHandler({OAuthUnlinkFailureException.class, UnAuthenticationException.class,
        InvalidJwtException.class})
    public ResponseEntity<ErrorTemplate> authenticationFailExHandle(AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorTemplate.of(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorTemplate> duplicateUsernameExHandle(AuthException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorTemplate.of(ex.getMessage()));
    }
}
