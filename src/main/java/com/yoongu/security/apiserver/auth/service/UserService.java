package com.yoongu.security.apiserver.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yoongu.security.apiserver.auth.dto.UserDto;
import com.yoongu.security.apiserver.auth.dto.UserSearchCondition;
import com.yoongu.security.apiserver.auth.dto.request.UserCommonRequest;
import com.yoongu.security.apiserver.auth.dto.request.UserCreationRequest;
import com.yoongu.security.apiserver.auth.dto.request.UserModificationRequest;
import com.yoongu.security.apiserver.auth.exception.AlreadyUsedPasswordException;
import com.yoongu.security.apiserver.auth.mapper.UserMapper;
import com.yoongu.security.apiserver.common.pagination.PageResponse;
import com.yoongu.security.persistence.auth.User;
import com.yoongu.security.persistence.auth.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserDataService userDataService;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getUserByUserName(String userName) {
        return userDataService.getByUserName(userName);
    }

    @Transactional
    public void save(UserCreationRequest userCreationRequest) {
        User user = User.builder()
            .userName(userCreationRequest.getUserName())
            .password(passwordEncoder.encode(userCreationRequest.getPassword()))
            .nickName(userCreationRequest.getNickName())
            .loginFailureCount(0)
            .isAccountNonExpired(true)
            .isAccountNonLocked(true)
            .isCredentialsNonExpired(true)
            .isEnabled(true)
            .role(userCreationRequest.getRole())
            .loginAcceptedDate(userCreationRequest.getLoginAcceptedDate())
            .build();
        userDataService.save(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserDto> getUsers(Pageable pageable, UserSearchCondition userSearchCondition) {
        Page<UserDto> users = userDataService.getUsers(pageable, userSearchCondition)
            .map(UserMapper::toUserDto);
        return new PageResponse<>(users.stream().collect(Collectors.toList()),
            pageable.getPageNumber() + 1, pageable.getPageSize(), users.getTotalPages());
    }

    @Transactional
    public void modify(UserModificationRequest modificationRequest) {
        userDataService.updateUser(modificationRequest);
    }

    @Transactional
    public void reset(UserCommonRequest commonRequest) {
        boolean result = validatePasswordHistory(commonRequest);
        if (result) {
            String encryptedPassword = passwordEncoder.encode(commonRequest.getPassword());
            commonRequest.setPassword(encryptedPassword);
            userDataService.resetUser(commonRequest);
        } else {
            throw new AlreadyUsedPasswordException("invalid password. already used in password history(12)");
        }
    }

    @Transactional
    public void changePassword(UserCommonRequest commonRequest) {
        boolean result = this.validatePasswordHistory(commonRequest);
        if (result) {
            String encryptedPassword = passwordEncoder.encode(commonRequest.getPassword());
            commonRequest.setPassword(encryptedPassword);
            userDataService.changePassword(commonRequest);
        } else {
            throw new AlreadyUsedPasswordException("invalid password. already used in password history(12)");
        }
    }

    private boolean validatePasswordHistory(UserCommonRequest commonRequest) {
        String userName = commonRequest.getUserName();
        User user = userDataService.getByUserName(userName);

        JsonNode lastTwelvePasswordHistory = user.getLastTwelvePasswordHistory();

        for (JsonNode passwordHistory : lastTwelvePasswordHistory) {
            String oldEncryptedPassword = passwordHistory.get("password").asText();
            if (passwordEncoder.matches(commonRequest.getPassword(), oldEncryptedPassword)) {
                return false;
            }
        }

        if (lastTwelvePasswordHistory.size() == 12) {
            ((ArrayNode) lastTwelvePasswordHistory).remove(0);
        }

        return true;
    }

    @Transactional
    public void delete(String userName) {
        userDataService.deleteUser(userName);
    }

    @Transactional
    public void doLoginProcess(String userName) {
        userDataService.doLoginProcess(userName);
    }

    @Transactional
    public void lockUserAccount(String userName) {
        userDataService.lockUserAccount(userName);
    }

    @Transactional
    public void increaseLoginFailureCount(String userName) {
        userDataService.increaseLoginFailureCount(userName);
    }
}
