package com.yoongu.security.apiserver.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class PasswordExpiredException extends AuthenticationException {

    public PasswordExpiredException(String message) {
        super(message);
    }

}
