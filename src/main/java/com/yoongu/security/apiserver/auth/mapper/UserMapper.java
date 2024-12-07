package com.yoongu.security.apiserver.auth.mapper;

import com.yoongu.security.apiserver.auth.dto.UserDto;
import com.yoongu.security.persistence.auth.User;

import java.time.LocalDate;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
            .userName(user.getUserName())
            .nickName(user.getNickName())
            .isAccountNonExpired(LocalDate.now().isBefore(user.getLoginAcceptedDate().plusDays(1)))
            .isAccountNonLocked(user.isAccountNonLocked())
            .isCredentialsNonExpired(user.isCredentialsNonExpired())
            .isEnabled(user.isEnabled())
            .role(user.getRole())
            .createdDate(user.getCreatedDate())
            .lastLoginDate(user.getLastLoginDate())
            .lastPasswordModifiedDate(user.getLastPasswordModifiedDate())
            .loginAcceptedDate(user.getLoginAcceptedDate())
            .build();
    }
}
