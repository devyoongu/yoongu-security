package com.yoongu.security.apiserver.auth.dto.request;

import com.yoongu.security.apiserver.auth.controller.validation.StrongPassword;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserCommonRequest {

    @NotEmpty
    protected String userName;

    @NotEmpty
    @StrongPassword
    protected String password;

}
