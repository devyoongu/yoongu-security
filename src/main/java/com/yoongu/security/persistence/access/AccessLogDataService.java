package com.yoongu.security.persistence.access;

import com.yoongu.security.apiserver.access.dto.AccessLogSearchCondition;
import com.yoongu.security.apiserver.access.dto.AccessLogSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessLogDataService {

    private final AccessLogRepository userAccessLogRepository;

    public AccessLog save(AccessLog accessLog) {
        return userAccessLogRepository.save(accessLog);
    }

    public Page<AccessLog> getBySearchCondition(Pageable pageable, AccessLogSearchCondition searchCondition) {
        return userAccessLogRepository.findBySearchCondition(pageable, searchCondition);
    }

    public Page<AccessLog> getBySearchRequest(Pageable pageable, AccessLogSearchRequest searchRequest) {
        return userAccessLogRepository.findBySearchRequest(pageable, searchRequest);
    }

}
