package com.yoongu.security.apiserver.auth.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoongu.security.apiserver.access.mapper.AccessLogMapper;
import com.yoongu.security.apiserver.access.service.AccessLogService;
import com.yoongu.security.apiserver.auth.exception.AnotherLoginException;
import com.yoongu.security.apiserver.auth.exception.SecurityErrorCode;
import com.yoongu.security.apiserver.auth.service.UserService;
import com.yoongu.security.apiserver.common.error.ErrorResponse;
import com.yoongu.security.persistence.access.AccessLog;
import com.yoongu.security.persistence.auth.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    private final AccessLogService accessLogService;

    private final UserService userService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        RuntimeException exception = (RuntimeException) request.getAttribute("exception");
        log.debug("JWT invalid : {}", exception.getMessage());

        SecurityErrorCode errorCode = getErrorCode(exception);
        setResponse(response, errorCode);

        String userName = request.getHeader("x-request-id");
        User user = userService.getUserByUserName(userName);
        AccessLog accessLog = AccessLogMapper.createAccessLog(request, response);
        accessLog.updateNickName(user.getNickName());
        accessLogService.save(accessLog);
    }

    private SecurityErrorCode getErrorCode(RuntimeException e) {
        if (e instanceof ExpiredJwtException) {
            return SecurityErrorCode.TOKEN_EXPIRED;
        }

        if (e instanceof SignatureException) {
            return SecurityErrorCode.SIGNATURE_NOT_MATCH;
        }

        if(e instanceof AnotherLoginException) {
            return SecurityErrorCode.ANOTHER_LOGIN;
        }

        return SecurityErrorCode.TOKEN_INVALID;
    }

    private void setResponse(HttpServletResponse response, SecurityErrorCode errorCode) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        OutputStream out = response.getOutputStream();
        objectMapper.writeValue(out, errorResponse);
        out.flush();
    }
}
