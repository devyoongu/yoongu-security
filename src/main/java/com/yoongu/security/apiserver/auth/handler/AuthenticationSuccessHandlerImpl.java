package com.yoongu.security.apiserver.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.yoongu.security.apiserver.access.mapper.AccessLogMapper;
import com.yoongu.security.apiserver.access.service.AccessLogService;
import com.yoongu.security.apiserver.auth.TokenProvider;
import com.yoongu.security.apiserver.auth.dto.response.TokenDto;
import com.yoongu.security.persistence.access.AccessLog;
import com.yoongu.security.persistence.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    private final Cache<String, String> userCache;

    private final AccessLogService accessLogService;

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        String accessToken = tokenProvider.generateJwtToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);
        String cacheKey = user.getUserName() + ":guid";

        userCache.put(user.getUserName(), accessToken);
        userCache.put(cacheKey, request.getHeader("x-request-guid"));

        TokenDto tokenDto = TokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
        setTokenResponse(response, tokenDto);

        AccessLog accessLog = AccessLogMapper.createAccessLog(request, response);
        accessLog.updateNickName(user.getNickName());
        accessLogService.save(accessLog);
    }

    private void setTokenResponse(HttpServletResponse response, TokenDto tokenDto) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        ServletOutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, tokenDto);
        outputStream.flush();
    }
}
