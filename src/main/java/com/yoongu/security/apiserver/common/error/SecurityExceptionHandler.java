package com.yoongu.security.apiserver.common.error;

import com.yoongu.security.apiserver.auth.exception.AlreadyUsedPasswordException;
import com.yoongu.security.apiserver.auth.exception.AnotherLoginException;
import com.yoongu.security.apiserver.auth.exception.SecurityErrorCode;
import com.yoongu.security.apiserver.auth.exception.UnauthorizedException;
import com.yoongu.security.apiserver.auth.exception.UserExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return SecurityGlobalExceptionHandler.getErrorResponse(CommonErrorCode.ACCESS_DENY, e.getMessage());
    }

    @ExceptionHandler(AlreadyUsedPasswordException.class)
    protected ResponseEntity<ErrorResponse> handleAlreadyUsedPasswordException(AlreadyUsedPasswordException e) {
        return SecurityGlobalExceptionHandler.getErrorResponse(SecurityErrorCode.ALREADY_USED_PASSWORD_IN_HISTORY, e.getMessage());
    }

    @ExceptionHandler(AnotherLoginException.class)
    protected ResponseEntity<ErrorResponse> handleAnotherLoginException(AnotherLoginException e) {
        return SecurityGlobalExceptionHandler.getErrorResponse(SecurityErrorCode.ANOTHER_LOGIN, e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        return SecurityGlobalExceptionHandler.getErrorResponse(SecurityErrorCode.TOKEN_EXPIRED, e.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<ErrorResponse> handleJwtException(JwtException e) {
        return SecurityGlobalExceptionHandler.getErrorResponse(SecurityErrorCode.TOKEN_INVALID, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        return SecurityGlobalExceptionHandler.getErrorResponse(SecurityErrorCode.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UserExpiredException.class)
    protected ResponseEntity<ErrorResponse> handleUserExpiredException(UserExpiredException e) {
        return SecurityGlobalExceptionHandler.getErrorResponse(SecurityErrorCode.USER_EXPIRED, e.getMessage());
    }

}
