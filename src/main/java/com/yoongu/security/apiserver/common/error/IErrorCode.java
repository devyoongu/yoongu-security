package com.yoongu.security.apiserver.common.error;

import org.springframework.http.HttpStatus;

public interface IErrorCode {

    String getCode();

    HttpStatus getHttpStatus();

    String getMessage();
}
