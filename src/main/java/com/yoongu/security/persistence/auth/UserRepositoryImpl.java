package com.yoongu.security.persistence.auth;

import com.yoongu.security.apiserver.auth.dto.UserSearchCondition;
import com.yoongu.security.apiserver.common.enums.TimeSortOrder;
import com.yoongu.security.apiserver.common.enums.UserSearchType;
import com.yoongu.security.apiserver.common.error.InvalidParamException;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.List;


@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> findByCondition(Pageable pageable, UserSearchCondition condition) {
        List<User> results = queryFactory
            .selectFrom(QUser.user)
            .where(createDynamicSearchText(condition.getSearchType(), condition.getSearchText()))
            .orderBy(orderBy(condition.getTimeSortOrder()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<User> countQuery = queryFactory
            .selectFrom(QUser.user)
            .from(QUser.user)
            .where(createDynamicSearchText(condition.getSearchType(), condition.getSearchText()));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchCount);
    }

    private BooleanExpression createDynamicSearchText(UserSearchType searchType, String searchText) {
        switch (searchType) {
            case ALL:
                return null;
            case USER_NAME:
                return StringUtils.isNotBlank(searchText) ? QUser.user.userName.contains(searchText) : null;
            case NICK_NAME:
                return StringUtils.isNotBlank(searchText) ? QUser.user.nickName.contains(searchText) : null;
            default:
                throw new InvalidParamException("Not supported search type, " + searchText);
        }
    }

    private OrderSpecifier<?> orderBy(TimeSortOrder timeSortOrder) {
        if (timeSortOrder == TimeSortOrder.ASC) {
            return QUser.user.createdDate.asc();
        } else {
            return QUser.user.createdDate.desc();
        }
    }

}
