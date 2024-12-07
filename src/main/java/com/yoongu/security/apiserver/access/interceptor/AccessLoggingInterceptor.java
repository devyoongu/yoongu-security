package com.yoongu.security.apiserver.access.interceptor;

import com.yoongu.security.apiserver.access.service.AccessLogService;
import com.yoongu.security.apiserver.auth.TokenProvider;
import com.yoongu.security.apiserver.auth.service.UserService;
import com.yoongu.security.persistence.access.AccessLog;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yoongu.security.persistence.auth.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessLoggingInterceptor implements HandlerInterceptor {

    private final AccessLogService accessLogService;

    private final TokenProvider tokenProvider;

    private final UserService userService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        accessLogService.save(createAccessLog(request, response));
    }

    private AccessLog createAccessLog(HttpServletRequest request, HttpServletResponse response) {
        String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = tokenProvider.getTokenFromAuthHeaderValue(authHeaderValue);
        String userName = tokenProvider.getUserNameFromToken(token);
        String userIp = request.getRemoteAddr();
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();
        int httpStatusCode = response.getStatus();

        User user = userService.getUserByUserName(userName);

        return AccessLog.builder()
            .userName(userName)
            .userIp(userIp)
            .nickName(user.getNickName())
            .requestMethod(requestMethod)
            .requestUrl(requestUrl)
            .httpStatusCode(httpStatusCode)
            .build();
    }
}
