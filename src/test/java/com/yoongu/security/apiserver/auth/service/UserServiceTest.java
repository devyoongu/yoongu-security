package com.yoongu.security.apiserver.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yoongu.security.apiserver.auth.dto.UserDto;
import com.yoongu.security.apiserver.auth.dto.UserSearchCondition;
import com.yoongu.security.apiserver.auth.dto.request.UserCommonRequest;
import com.yoongu.security.apiserver.auth.dto.request.UserCreationRequest;
import com.yoongu.security.apiserver.auth.dto.request.UserModificationRequest;
import com.yoongu.security.apiserver.auth.exception.AlreadyUsedPasswordException;
import com.yoongu.security.apiserver.common.SpringProfile;
import com.yoongu.security.apiserver.common.enums.UserSearchType;
import com.yoongu.security.apiserver.common.pagination.PageRequest;
import com.yoongu.security.apiserver.common.pagination.PageResponse;
import com.yoongu.security.persistence.auth.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles(SpringProfile.TEST)
@Sql("/sql/data.sql")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("유저명으로 유저를 조회한다.")
    public void loadUserByUsername() {
        // Given
        String userName = "miller";
        String plainPassword = "miller2017@";

        // When
        User user = userService.getUserByUserName(userName);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUserName()).isEqualTo("miller");
        assertThat(user.getRole()).isEqualTo(User.UserRole.ROLE_USER);
        assertThat(passwordEncoder.matches(plainPassword, user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("유저를 데이터베이스에 저장한다.")
    public void saveUser() {
        // Given
        String userName = "유안상";
        String plainPassword = "miller2017@";
        UserCreationRequest requestDto = UserCreationRequest.builder()
            .userName(userName)
            .password(plainPassword)
            .nickName(userName)
            .role(User.UserRole.ROLE_USER)
            .loginAcceptedDate(LocalDate.now().plusYears(5))
            .build();

        // When
        userService.save(requestDto);
        User user = userService.getUserByUserName(userName);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUserName()).isEqualTo(userName);
        assertThat(user.getRole()).isEqualTo(User.UserRole.ROLE_USER);
        assertThat(passwordEncoder.matches(plainPassword, user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("등록된 모든 유저목록을 가져온다.")
    public void getUsers() {
        // Given
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(1);
        pageRequest.setPageSize(20);

        UserSearchCondition userSearchCondition = new UserSearchCondition();

        // When
        PageResponse<UserDto> users = userService.getUsers(pageRequest.of(), userSearchCondition);
        List<UserDto> contents = users.getContents();

        // Then
        assertThat(contents).isNotNull();
        assertThat(contents.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("유저이름으로 유저목록을 조회한다.")
    public void getUsers_searchByUserName() {
        // Given
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(1);
        pageRequest.setPageSize(20);

        UserSearchCondition userSearchCondition = new UserSearchCondition();
        userSearchCondition.setSearchType(UserSearchType.USER_NAME);
        userSearchCondition.setSearchText("admin");

        // When
        PageResponse<UserDto> users = userService.getUsers(pageRequest.of(), userSearchCondition);
        List<UserDto> contents = users.getContents();

        // Then
        assertThat(contents).isNotNull();
        assertThat(contents.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("유저 정보를 수정한다.")
    public void modifyUser() {
        // Given
        String userName = "miller";
        String nickName = "modify";
        UserModificationRequest requestDto = UserModificationRequest.builder()
            .role(User.UserRole.ROLE_ADMIN)
            .loginAcceptedDate(LocalDate.of(2022, 12, 12))
            .nickName(nickName)
            .userName(userName)
            .build();

        // When
        userService.modify(requestDto);
        User user = userService.getUserByUserName(userName);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUserName()).isEqualTo(userName);
        assertThat(user.getRole()).isEqualTo(User.UserRole.ROLE_ADMIN);
        assertThat(user.getNickName()).isEqualTo(nickName);
        assertThat(user.getLoginAcceptedDate()).isEqualTo(LocalDate.of(2022, 12, 12));
    }

    @Test
    @DisplayName("유저 상태를 초기화한다.")
    public void resetUser() {
        // Given
        String userName = "miller";
        String plainPassword = "changePassword";
        UserCommonRequest requestDto = UserCommonRequest.builder()
            .userName(userName)
            .password(plainPassword)
            .build();

        // When
        userService.reset(requestDto);
        User user = userService.getUserByUserName(userName);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUserName()).isEqualTo(userName);
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(passwordEncoder.matches(plainPassword, user.getPassword())).isTrue();
        assertThat(user.getLoginFailureCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("유저의 패스워드를 변경한다. (두번 변경하되 다른 패스워드로 정상 동작하는지까지 확인)")
    public void changePassword() {
        // Given
        String userName = "miller";
        String plainPassword = "changePassword";
        UserCommonRequest requestDto = UserCommonRequest.builder()
            .userName(userName)
            .password(plainPassword)
            .build();

        // When
        userService.changePassword(requestDto);
        User user = userService.getUserByUserName(userName);

        // Then
        assertThat(passwordEncoder.matches(plainPassword, user.getPassword())).isTrue();

        // given
        String plainPassword2 = "changePassword2";
        requestDto.setPassword(plainPassword2);

        userService.changePassword(requestDto);
        user = userService.getUserByUserName(userName);

        // Then
        assertThat(passwordEncoder.matches(plainPassword2, user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("유저의 패스워드를 변경에 실패한다.(과거에 사용기록 있음)")
    public void changePasswordFailure() {
        // Given
        String userName = "miller";
        String plainPassword = "changePassword";
        UserCommonRequest requestDto = UserCommonRequest.builder()
            .userName(userName)
            .password(plainPassword)
            .build();

        // When & Then
        userService.changePassword(requestDto);
        requestDto.setPassword(plainPassword);
        assertThatThrownBy(() -> userService.changePassword(requestDto)).isInstanceOf(AlreadyUsedPasswordException.class);
    }

    @Test
    @DisplayName("유저를 데이터베이스에서 삭제한다.")
    public void deleteUser() {
        // Given
        String userName = "miller";

        // When
        userService.delete(userName);

        // Then
        assertThatThrownBy(() -> userService.getUserByUserName(userName)).isInstanceOf(UsernameNotFoundException.class);

    }

    @Test
    @DisplayName("로그인 성공 프로세스를 진행한다.")
    public void doLoginProcess() {
        // Given
        String userName = "miller";

        // When
        userService.doLoginProcess(userName);
        User user = userService.getUserByUserName(userName);

        // Then
        assertThat(user.getLoginFailureCount()).isEqualTo(0);
        assertThat(user.getLastLoginDate()).isNotNull();
    }

    @Test
    @DisplayName("유저 계정을 잠금 상태로 변경한다.")
    public void lockUserAccount() {
        // Given
        String userName = "miller";

        // When
        userService.lockUserAccount(userName);
        User user = userService.getUserByUserName(userName);

        // Then
        assertThat(user.isAccountNonLocked()).isFalse();
    }

    @Test
    @DisplayName("로그인 실패 카운트를 올린다.")
    public void increaseLoginFailureCount() {
        // Given
        String userName = "miller";

        // When
        for (int i = 0; i < 5; i++) {
            userService.increaseLoginFailureCount(userName);
        }
        User userDetails = userService.getUserByUserName(userName);

        // Then
        assertThat(userDetails.getLoginFailureCount()).isEqualTo(5);
    }
}