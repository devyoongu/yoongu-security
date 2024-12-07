package com.yoongu.security.apiserver.auth.controller;

import com.yoongu.security.apiserver.auth.dto.UserDto;
import com.yoongu.security.apiserver.auth.dto.UserSearchCondition;
import com.yoongu.security.apiserver.auth.dto.request.UserCommonRequest;
import com.yoongu.security.apiserver.auth.dto.request.UserCreationRequest;
import com.yoongu.security.apiserver.auth.dto.request.UserModificationRequest;
import com.yoongu.security.apiserver.auth.dto.response.TokenDto;
import com.yoongu.security.apiserver.auth.exception.SecurityErrorCode;
import com.yoongu.security.apiserver.auth.service.TokenService;
import com.yoongu.security.apiserver.auth.service.UserService;
import com.yoongu.security.apiserver.common.error.ErrorResponse;
import com.yoongu.security.apiserver.common.error.SecurityGlobalExceptionHandler;
import com.yoongu.security.apiserver.common.pagination.PageRequest;
import com.yoongu.security.apiserver.common.pagination.PageResponse;
import com.yoongu.security.persistence.auth.User.UserRole.Code;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;

    private final TokenService tokenService;

    @PostMapping("/admin/v1/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(Code.ADMIN)
    public void createUser(@RequestBody @Valid UserCreationRequest creationRequest) {
        userService.save(creationRequest);
    }

    @GetMapping("/admin/v1/auth/users")
    @ResponseStatus(HttpStatus.OK)
    @Secured(Code.ADMIN)
    public PageResponse<UserDto> getUsers(@ModelAttribute PageRequest pageRequest, UserSearchCondition userSearchCondition) {
        return userService.getUsers(pageRequest.of(), userSearchCondition);
    }

    @PutMapping("/admin/v1/auth/users")
    @ResponseStatus(HttpStatus.OK)
    @Secured(Code.ADMIN)
    public void modifyUser(@RequestBody @Valid UserModificationRequest modificationRequest) {
        userService.modify(modificationRequest);
    }

    @PutMapping("/admin/v1/auth/users/reset")
    @ResponseStatus(HttpStatus.OK)
    @Secured({Code.ADMIN, Code.USER})
    public void resetUser(@RequestBody @Valid UserCommonRequest commonRequest) {
        userService.reset(commonRequest);
    }

    @PutMapping("/admin/v1/auth/users/password")
    @ResponseStatus(HttpStatus.OK)
    @Secured({Code.USER, Code.ADMIN})
    public void changePasswordAtFirstLogin(@RequestBody @Valid UserCommonRequest commonRequest) {
        userService.changePassword(commonRequest);
    }

    @DeleteMapping("/admin/v1/auth/users/{userName}")
    @ResponseStatus(HttpStatus.OK)
    @Secured(Code.ADMIN)
    public void deleteUser(@PathVariable String userName) {
        userService.delete(userName);
    }

    @GetMapping("/admin/v1/auth/token/refresh")
    public ResponseEntity<TokenDto> refreshToken(@RequestHeader("Authorization") String authHeaderValue) {
        TokenDto reissueToken = tokenService.reissueToken(authHeaderValue);
        return ResponseEntity.ok(reissueToken);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponse> handleException(ExpiredJwtException e) {
        return SecurityGlobalExceptionHandler.getErrorResponse(SecurityErrorCode.TOKEN_EXPIRED, e.getMessage());
    }
}
