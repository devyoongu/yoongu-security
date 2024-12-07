package com.yoongu.security.apiserver.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class UserExpiredException extends AuthenticationException {

    public UserExpiredException(String message) {
        super(message);
    }

}
