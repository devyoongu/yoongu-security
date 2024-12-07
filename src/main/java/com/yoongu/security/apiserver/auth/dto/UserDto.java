package com.yoongu.security.apiserver.auth.dto;

import com.yoongu.security.persistence.auth.User.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String userName;

    private String nickName;

    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    private boolean isEnabled;

    private UserRole role;

    private LocalDateTime createdDate;

    private LocalDateTime lastLoginDate;

    private LocalDateTime lastPasswordModifiedDate;

    private LocalDate loginAcceptedDate;

}
