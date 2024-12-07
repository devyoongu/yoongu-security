package com.yoongu.security.apiserver.access.mapper;


import com.yoongu.security.persistence.access.AccessLog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessLogMapper {

    public static AccessLog createAccessLog(HttpServletRequest request, HttpServletResponse response) {
        return AccessLog.builder()
            .userName(request.getHeader("x-request-id"))
            .userIp(request.getRemoteAddr())
            .requestMethod(request.getMethod())
            .requestUrl(request.getRequestURI())
            .httpStatusCode(response.getStatus())
            .build();
    }
}
