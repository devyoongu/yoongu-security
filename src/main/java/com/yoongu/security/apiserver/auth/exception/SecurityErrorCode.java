package com.yoongu.security.apiserver.auth.exception;

import com.yoongu.security.apiserver.common.error.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode implements IErrorCode {
    // auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "auth-001", "Unauthorized Request"),
    USER_LOCKED(HttpStatus.UNAUTHORIZED, "auth-002", "User Account is Locked"),
    USER_EXPIRED(HttpStatus.UNAUTHORIZED, "auth-003", "User Account is Expired"),
    BAD_CREDENTIAL(HttpStatus.UNAUTHORIZED, "auth-004", "Bad Credential(Invalid Password)"),
    ANOTHER_LOGIN(HttpStatus.UNAUTHORIZED, "auth-005", "Another User Login"),
    ALREADY_USED_PASSWORD_IN_HISTORY(HttpStatus.UNAUTHORIZED, "auth-006", "Already used in password history"),
    UNKNOWN_USER(HttpStatus.UNAUTHORIZED, "auth-007", "Unknown user name"),
    PASSWORD_EXPIRED(HttpStatus.UNAUTHORIZED, "auth-008", "User Password is Expired"),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "auth-009", "Access denied"),

    // token
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "token-001", "Token is Expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "token-002", "Invalid Token"),
    SIGNATURE_NOT_MATCH(HttpStatus.UNAUTHORIZED, "token-003", "The signature does not match the token");


    private final HttpStatus httpStatus;

    private final String code;

    private final String message;

}
