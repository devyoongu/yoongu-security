package com.yoongu.security.persistence.access;

import com.yoongu.security.apiserver.access.dto.AccessLogSearchCondition;
import com.yoongu.security.apiserver.access.dto.AccessLogSearchRequest;
import com.yoongu.security.apiserver.common.enums.AccessLogSearchType;
import com.yoongu.security.apiserver.common.enums.TimeSortOrder;
import com.yoongu.security.apiserver.common.error.InvalidParamException;
import com.yoongu.security.apiserver.common.util.DateUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AccessLogRepositoryImpl implements AccessLogRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<AccessLog> findBySearchCondition(Pageable pageable, AccessLogSearchCondition searchCondition) {
        List<AccessLog> results = jpaQueryFactory
            .selectFrom(QAccessLog.accessLog)
            .where(
                eqUserName(searchCondition.getUserName()),
                betweenTimeStamp(searchCondition.getSearchStartTimestamp(), searchCondition.getSearchEndTimestamp())
            )
            .orderBy(orderBy(searchCondition.getTimeSortOrder()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<AccessLog> countQuery = this.jpaQueryFactory
            .select(QAccessLog.accessLog)
            .from(QAccessLog.accessLog)
            .where(
                eqUserName(searchCondition.getUserName()),
                betweenTimeStamp(searchCondition.getSearchStartTimestamp(), searchCondition.getSearchEndTimestamp())
            );

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchCount);
    }

    @Override
    public Page<AccessLog> findBySearchRequest(Pageable pageable, AccessLogSearchRequest searchRequest) {
        List<AccessLog> results = jpaQueryFactory
            .selectFrom(QAccessLog.accessLog)
            .where(
                createDynamicSearchText(searchRequest.getSearchType(), searchRequest.getSearchText()),
                betweenTimeStamp(searchRequest.getSearchStartTimestamp(), searchRequest.getSearchEndTimestamp())
            )
            .orderBy(orderBy(searchRequest.getTimeSortOrder()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<AccessLog> countQuery = this.jpaQueryFactory
            .select(QAccessLog.accessLog)
            .from(QAccessLog.accessLog)
            .where(
                createDynamicSearchText(searchRequest.getSearchType(), searchRequest.getSearchText()),
                betweenTimeStamp(searchRequest.getSearchStartTimestamp(), searchRequest.getSearchEndTimestamp())
            );

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchCount);
    }

    private BooleanExpression createDynamicSearchText(AccessLogSearchType searchType, String searchText) {
        switch (searchType) {
            case ALL:
                return null;
            case USER_NAME:
                return StringUtils.isNotBlank(searchText) ? QAccessLog.accessLog.userName.contains(searchText) : null;
            case NICK_NAME:
                return StringUtils.isNotBlank(searchText) ? QAccessLog.accessLog.nickName.contains(searchText) : null;
            case USER_IP:
                return StringUtils.isNotBlank(searchText) ? QAccessLog.accessLog.userIp.contains(searchText) : null;
            default:
                throw new InvalidParamException("Not supported search type, " + searchText);
        }
    }

    private BooleanExpression eqUserName(String userName) {
        return StringUtils.isNotBlank(userName) ? QAccessLog.accessLog.userName.eq(userName) : null;
    }

    private BooleanExpression betweenTimeStamp(Long searchStartTimestamp, Long searchEndTimestamp) {
        LocalDateTime startLocalDateTime = DateUtil.convertLongMillsToLocalDateTime(searchStartTimestamp);
        LocalDateTime endLocalDateTime = DateUtil.convertLongMillsToLocalDateTime(searchEndTimestamp);
        return QAccessLog.accessLog.createdDate.between(startLocalDateTime, endLocalDateTime);
    }

    private OrderSpecifier<?> orderBy(TimeSortOrder timeSortOrder) {
        if (timeSortOrder == TimeSortOrder.ASC) {
            return QAccessLog.accessLog.createdDate.asc();
        } else {
            return QAccessLog.accessLog.createdDate.desc();
        }
    }

}
