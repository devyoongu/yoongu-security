package com.yoongu.security.apiserver.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yoongu.security.apiserver.auth.dto.request.UserCreationRequest;
import com.yoongu.security.apiserver.auth.dto.request.UserModificationRequest;
import com.yoongu.security.apiserver.auth.dto.response.TokenDto;
import com.yoongu.security.apiserver.auth.service.TokenService;
import com.yoongu.security.apiserver.auth.service.UserService;
import com.yoongu.security.apiserver.common.SpringProfile;
import com.yoongu.security.persistence.auth.User.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
    value = AuthController.class,
    useDefaultFilters = false,
    includeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = AuthController.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(SpringProfile.TEST)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userDetailsService;

    @MockBean
    private TokenService tokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("유저 생성 성공 테스트 (모든 Validation 만족)")
    public void registerUserSuccessTest() throws Exception {
        // given
        String userName = "userName";
        String password = "Passw0rd@#$";
        String nickName = "nickName";
        String acceptedDate = "2021-12-15";
        UserRole userRole = UserRole.ROLE_ADMIN;

        ObjectNode request = objectMapper.createObjectNode();
        request.put("userName", userName);
        request.put("password", password);
        request.put("nickName", nickName);
        request.put("loginAcceptedDate", acceptedDate);
        request.put("role", userRole.getAuthority());

        UserCreationRequest userDto = UserCreationRequest.builder()
            .userName(userName)
            .password(password)
            .nickName(nickName)
            .build();

        doNothing().when(this.userDetailsService).save(userDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/admin/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print());

        // then
        resultActions
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("유저 생성 실패 테스트 (Password Validation 불만족)")
    public void registerUserFailureTest() throws Exception {
        // given
        String userName = "userName";
        String password = "password";
        String nickName = "nickName";
        String acceptedDate = "2021-12-15";
        UserRole userRole = UserRole.ROLE_ADMIN;

        ObjectNode request = objectMapper.createObjectNode();
        request.put("userName", userName);
        request.put("password", password);
        request.put("nickName", nickName);
        request.put("loginAcceptedDate", acceptedDate);
        request.put("role", userRole.getAuthority());

        UserCreationRequest userDto = UserCreationRequest.builder()
            .userName(userName)
            .password(password)
            .nickName(nickName)
            .build();

        doNothing().when(this.userDetailsService).save(userDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/admin/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print());

        // then
        resultActions
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 생성 실패 테스트 (UserName Validation 불만족)")
    public void registerUserFailureTest2() throws Exception {
        // given
        String userName = "";
        String password = "Passw0rd@#$";
        String nickName = "nickName";
        String acceptedDate = "2021-12-15";
        UserRole userRole = UserRole.ROLE_ADMIN;

        ObjectNode request = objectMapper.createObjectNode();
        request.put("userName", userName);
        request.put("password", password);
        request.put("nickName", nickName);
        request.put("loginAcceptedDate", acceptedDate);
        request.put("role", userRole.getAuthority());

        UserCreationRequest userDto = UserCreationRequest.builder()
            .userName(userName)
            .password(password)
            .nickName(nickName)
            .build();

        doNothing().when(this.userDetailsService).save(userDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/admin/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print());

        // then
        resultActions
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 생성 실패 테스트 (nickName Validation 불만족)")
    public void registerUserFailureTest3() throws Exception {
        // given
        String userName = "userName";
        String password = "Passw0rd@#$";
        String nickName = "";
        String acceptedDate = "2021-12-15";
        UserRole userRole = UserRole.ROLE_ADMIN;

        ObjectNode request = objectMapper.createObjectNode();
        request.put("userName", userName);
        request.put("password", password);
        request.put("nickName", nickName);
        request.put("loginAcceptedDate", acceptedDate);
        request.put("role", userRole.getAuthority());

        UserCreationRequest userDto = UserCreationRequest.builder()
            .userName(userName)
            .password(password)
            .nickName(nickName)
            .build();

        doNothing().when(this.userDetailsService).save(userDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/admin/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print());

        // then
        resultActions
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 수정 테스트")
    public void modifyUserTest() throws Exception {
        // given
        String userName = "admin";
        String nickName = "miller@test";
        UserRole userRole = UserRole.ROLE_ADMIN;

        ObjectNode request = objectMapper.createObjectNode();
        request.put("userName", userName);
        request.put("nickName", nickName);
        request.put("role", userRole.getAuthority());

        UserModificationRequest dto = UserModificationRequest.builder()
            .userName(userName)
            .nickName(nickName)
            .role(userRole)
            .build();

        doNothing().when(this.userDetailsService).modify(dto);

        // when
        ResultActions resultActions = mockMvc.perform(put("/admin/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print());

        // then
        resultActions
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Refresh Token 생성")
    public void reissue_token() throws Exception {
        // given
        TokenDto tokenDto = TokenDto.builder()
            .accessToken("test.access.token")
            .refreshToken("test.refresh.token")
            .build();
        given(tokenService.reissueToken(anyString())).willReturn(tokenDto);

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/admin/v1/auth/token/refresh")
                .header("Authorization", "Bearer test.refresh.token")
        );

        // then
        resultActions
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Refresh Token 생성 실패 - (토큰 시간 만료)")
    public void refresh_token_expired_when_reissue_token_request() throws Exception {
        // given
        given(tokenService.reissueToken(anyString())).willThrow(new ExpiredJwtException(null, null, "JWT expired"));

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/admin/v1/auth/token/refresh")
                .header("Authorization", "Bearer test.refresh.token")
        );

        // then
        resultActions
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(ExpiredJwtException.class))
            .andExpect(jsonPath("$.code").value("token-001"))
            .andExpect(jsonPath("$.message").value("Token is Expired"));
    }
}