package com.yoongu.security.apiserver.common.error;

public class InvalidParamException extends BusinessException {

    public InvalidParamException(String message) {
        super(CommonErrorCode.INVALID_INPUT_VALUE, message);
    }

}
