package com.yoongu.security.apiserver.access.service;

import com.yoongu.security.apiserver.access.dto.AccessLogDto;
import com.yoongu.security.apiserver.access.dto.AccessLogSearchCondition;
import com.yoongu.security.apiserver.access.dto.AccessLogSearchRequest;
import com.yoongu.security.apiserver.common.pagination.PageResponse;
import com.yoongu.security.persistence.access.AccessLog;
import com.yoongu.security.persistence.access.AccessLogDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final AccessLogDataService accessLogDataService;

    @Transactional
    public void save(AccessLog accessLog) {
        accessLogDataService.save(accessLog);
    }

    @Transactional(readOnly = true)
    public PageResponse<AccessLogDto> getBySearchCondition(Pageable pageable, AccessLogSearchCondition searchCondition) {
        Page<AccessLog> accessLogs = accessLogDataService.getBySearchCondition(pageable, searchCondition);
        return new PageResponse<>(createAccessLogList(accessLogs), pageable.getPageNumber() + 1, pageable.getPageSize(), accessLogs.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PageResponse<AccessLogDto> getBySearchRequest(Pageable pageable, AccessLogSearchRequest searchRequest) {
        Page<AccessLog> accessLogs = accessLogDataService.getBySearchRequest(pageable, searchRequest);
        return new PageResponse<>(createAccessLogList(accessLogs), pageable.getPageNumber() + 1, pageable.getPageSize(), accessLogs.getTotalPages());
    }

    private List<AccessLogDto> createAccessLogList(Page<AccessLog> accessLogs) {
        Page<AccessLogDto> accessLogDtoPage = accessLogs.map(this::convertToAccessLogDto);
        return accessLogDtoPage.stream().collect(Collectors.toList());
    }

    private AccessLogDto convertToAccessLogDto(AccessLog accessLog) {
        return AccessLogDto.builder()
            .requestMethod(accessLog.getRequestMethod())
            .requestUrl(accessLog.getRequestUrl())
            .userName(accessLog.getUserName())
            .userNickName(accessLog.getNickName())
            .userIp(accessLog.getUserIp())
            .createdDate(accessLog.getCreatedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }

}
