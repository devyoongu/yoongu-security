package com.yoongu.security.apiserver.auth.exception;

import com.yoongu.security.apiserver.common.error.BusinessException;

public class AnotherLoginException extends BusinessException {

    public AnotherLoginException(String message) {
        super(SecurityErrorCode.ANOTHER_LOGIN, message);
    }

}
