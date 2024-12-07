package com.yoongu.security.apiserver.access.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AccessLogDto {

    private final String requestMethod;

    private final String requestUrl;

    private final String userName;

    private final String userNickName;

    private final String userIp;

    private final String createdDate;
}
