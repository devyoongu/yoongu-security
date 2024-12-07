package com.yoongu.security.apiserver.auth.exception;

import com.yoongu.security.apiserver.common.error.BusinessException;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(SecurityErrorCode.UNAUTHORIZED, message);
    }

}
