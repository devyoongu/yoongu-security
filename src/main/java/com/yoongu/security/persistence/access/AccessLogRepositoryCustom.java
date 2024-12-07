package com.yoongu.security.persistence.access;

import com.yoongu.security.apiserver.access.dto.AccessLogSearchCondition;
import com.yoongu.security.apiserver.access.dto.AccessLogSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccessLogRepositoryCustom {

    Page<AccessLog> findBySearchCondition(Pageable pageable, AccessLogSearchCondition searchCondition);

    Page<AccessLog> findBySearchRequest(Pageable pageable, AccessLogSearchRequest searchRequest);

}
