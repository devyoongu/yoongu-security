package com.yoongu.security.persistence.access;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long>, AccessLogRepositoryCustom {

}
