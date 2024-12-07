package com.yoongu.security.apiserver.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements IErrorCode {

    SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "common-001", "Unspecified error"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "common-002", "Invalid Parameter"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "common-003", "Bad Request"),
    ACCESS_DENY(HttpStatus.FORBIDDEN, "common-004", "Access deny"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "common-005", "Missing Resource"),
    CONFLICT(HttpStatus.CONFLICT, "common-006", "Conflict Request"),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "common-007", "Not Implemented yet"),
    EXTERNAL_IO_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "common-008", "External communication failed"),
    RESOURCE_NOT_EXIST_ERROR(HttpStatus.NOT_FOUND, "common-009", "Resource Not Exist Error");

    private final HttpStatus httpStatus;

    private final String code;

    private final String message;

}
