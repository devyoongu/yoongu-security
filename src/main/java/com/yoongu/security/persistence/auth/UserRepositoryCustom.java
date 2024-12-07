package com.yoongu.security.persistence.auth;

import com.yoongu.security.apiserver.auth.dto.UserSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> findByCondition(Pageable pageable, UserSearchCondition userSearchCondition);

}
