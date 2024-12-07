package com.yoongu.security.apiserver.auth.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest httpServletRequest;

    @ParameterizedTest
    @ValueSource(strings = {
        "/admin/v1/auth/token/refresh",
        "/api/v1/auth/token/refresh",
        "/another",
    })
    @DisplayName("Jwt인증 필터를 거치지 않는 servletPath")
    public void should_not_filter(String servletPath) {
        // given
        given(httpServletRequest.getServletPath()).willReturn(servletPath);

        // when
        boolean result = jwtAuthenticationFilter.shouldNotFilter(httpServletRequest);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/admin/v1/departments",
        "/admin/v1/staffer"
    })
    @DisplayName("Jwt인증 필터를 거쳐야 하는 servletPath")
    public void should_filter(String servletPath) {
        // given
        given(httpServletRequest.getServletPath()).willReturn(servletPath);

        // when
        boolean result = jwtAuthenticationFilter.shouldNotFilter(httpServletRequest);

        // then
        assertThat(result).isFalse();
    }
}