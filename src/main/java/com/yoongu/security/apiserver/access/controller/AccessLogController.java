package com.yoongu.security.apiserver.access.controller;

import com.yoongu.security.apiserver.access.dto.AccessLogDto;
import com.yoongu.security.apiserver.access.dto.AccessLogSearchCondition;
import com.yoongu.security.apiserver.access.dto.AccessLogSearchRequest;
import com.yoongu.security.apiserver.access.service.AccessLogService;
import com.yoongu.security.apiserver.common.pagination.PageRequest;
import com.yoongu.security.apiserver.common.pagination.PageResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccessLogController {

    private final AccessLogService accessLogService;

    @GetMapping("/admin/v1/access/logs")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<AccessLogDto> getAccessLogs(@ModelAttribute PageRequest pageRequest, @ModelAttribute @Valid AccessLogSearchCondition searchCondition) {
        return accessLogService.getBySearchCondition(pageRequest.of(), searchCondition);
    }

    @GetMapping("/admin/v2/access/logs")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<AccessLogDto> getAccessLogs(@ModelAttribute PageRequest pageRequest, @ModelAttribute @Valid AccessLogSearchRequest searchRequest) {
        return accessLogService.getBySearchRequest(pageRequest.of(), searchRequest);
    }
}
