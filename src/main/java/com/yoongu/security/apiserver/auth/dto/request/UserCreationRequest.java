package com.yoongu.security.apiserver.auth.dto.request;

import com.yoongu.security.persistence.auth.User;
import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class UserCreationRequest extends UserCommonRequest {

    @NotEmpty
    private String nickName;

    @NotNull
    private User.UserRole role;

    private LocalDate loginAcceptedDate;

}
