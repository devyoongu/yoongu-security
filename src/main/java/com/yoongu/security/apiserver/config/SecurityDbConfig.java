package com.yoongu.security.apiserver.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityDbConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean("securityJpaQueryFactory")
    public JPAQueryFactory JpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

}
