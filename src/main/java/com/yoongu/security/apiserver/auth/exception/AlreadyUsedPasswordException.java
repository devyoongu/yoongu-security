package com.yoongu.security.apiserver.auth.exception;

import com.yoongu.security.apiserver.common.error.BusinessException;

public class AlreadyUsedPasswordException extends BusinessException {

    public AlreadyUsedPasswordException(String message) {
        super(SecurityErrorCode.ALREADY_USED_PASSWORD_IN_HISTORY, message);
    }

}
