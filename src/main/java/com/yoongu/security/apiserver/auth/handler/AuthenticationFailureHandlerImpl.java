package com.yoongu.security.apiserver.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoongu.security.apiserver.access.mapper.AccessLogMapper;
import com.yoongu.security.apiserver.access.service.AccessLogService;
import com.yoongu.security.apiserver.auth.exception.PasswordExpiredException;
import com.yoongu.security.apiserver.auth.exception.SecurityErrorCode;
import com.yoongu.security.apiserver.auth.exception.UserExpiredException;
import com.yoongu.security.apiserver.auth.service.UserService;
import com.yoongu.security.apiserver.common.error.ErrorResponse;
import com.yoongu.security.persistence.access.AccessLog;
import com.yoongu.security.persistence.auth.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    private final AccessLogService accessLogService;

    private final UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        OutputStream out = response.getOutputStream();
        ErrorResponse errorResponse = getErrorResponse(exception);
        objectMapper.writeValue(out, errorResponse);
        out.flush();

        log.info("Login fail: {}", exception.getMessage());

        String userName = request.getHeader("x-request-id");
        User user = userService.getUserByUserName(userName);
        AccessLog accessLog = AccessLogMapper.createAccessLog(request, response);
        accessLog.updateNickName(user.getNickName());
        accessLogService.save(accessLog);
    }

    private ErrorResponse getErrorResponse(RuntimeException e) {
        if (e instanceof LockedException) {
            return ErrorResponse.of(SecurityErrorCode.USER_LOCKED, e.getMessage());
        }

        if (e instanceof BadCredentialsException || e instanceof UsernameNotFoundException) {
            return ErrorResponse.of(SecurityErrorCode.BAD_CREDENTIAL, e.getMessage());
        }

        if (e instanceof UserExpiredException) {
            return ErrorResponse.of(SecurityErrorCode.USER_EXPIRED, e.getMessage());
        }

        if (e instanceof PasswordExpiredException) {
            return ErrorResponse.of(SecurityErrorCode.PASSWORD_EXPIRED, e.getMessage());
        }

        return ErrorResponse.of(SecurityErrorCode.UNKNOWN_USER);
    }
}
