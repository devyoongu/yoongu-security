package com.yoongu.security.apiserver.common.error;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class SecurityGlobalExceptionHandler {

    public static ResponseEntity<ErrorResponse> getErrorResponse(IErrorCode errorCode, String detail) {
        ErrorResponse response = ErrorResponse.of(errorCode, errorCode.getMessage(), detail);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return new ResponseEntity<>(ErrorResponse.from(e), e.getCode().getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return SecurityGlobalExceptionHandler.getErrorResponse(CommonErrorCode.SERVICE_ERROR, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return SecurityGlobalExceptionHandler.getErrorResponse(CommonErrorCode.SERVICE_ERROR, e.getMessage());
    }

    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    protected ResponseEntity<ErrorResponse> handleBindException(Exception e) {
        log.error(e.getMessage(), e);
        return SecurityGlobalExceptionHandler.getErrorResponse(CommonErrorCode.INVALID_INPUT_VALUE, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("field: [");
            builder.append(fieldError.getField());
            builder.append("]");
            builder.append(" input value: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }
        return SecurityGlobalExceptionHandler.getErrorResponse(CommonErrorCode.INVALID_INPUT_VALUE, builder.toString());
    }

    @ExceptionHandler(EntityExistsException.class)
    protected ResponseEntity<ErrorResponse> handleConflictException(Exception e) {
        log.error(e.getMessage(), e);
        return SecurityGlobalExceptionHandler.getErrorResponse(CommonErrorCode.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(ClientAbortException.class)
    protected void handleConflictException(ClientAbortException e, HttpServletRequest request) {
        log.warn("ClientAbortException {} {} from remote address {}", request.getMethod(), request.getRequestURL(), request.getRemoteAddr());
    }
}
