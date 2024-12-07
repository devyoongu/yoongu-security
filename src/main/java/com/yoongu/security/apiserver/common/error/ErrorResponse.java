package com.yoongu.security.apiserver.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ErrorResponse {

    private String code;

    private String message;

    private String detail;

    public static ErrorResponse of(IErrorCode errorCode) {
        return ErrorResponse.of(errorCode, errorCode.getMessage());
    }

    public static ErrorResponse of(IErrorCode errorCode, String message) {
        return ErrorResponse.of(errorCode, message, "");
    }

    public static ErrorResponse of(IErrorCode errorCode, String message, String detail) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(errorCode.getCode());
        errorResponse.setMessage(message);
        errorResponse.setDetail(detail);
        return errorResponse;
    }

    public static ErrorResponse from(BusinessException e) {
        return ErrorResponse.of(e.getCode(), e.getMessage(), e.getDetail());
    }
}
