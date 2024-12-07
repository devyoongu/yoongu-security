package com.yoongu.security.apiserver.auth.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.yoongu.security.persistence.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModificationRequest {

    @NotEmpty
    private String userName;

    @NotEmpty
    private String nickName;

    @NotNull
    private User.UserRole role;

    private LocalDate loginAcceptedDate;

}
