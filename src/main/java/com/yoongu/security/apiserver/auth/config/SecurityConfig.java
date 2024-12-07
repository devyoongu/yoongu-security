package com.yoongu.security.apiserver.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.yoongu.security.apiserver.access.service.AccessLogService;
import com.yoongu.security.apiserver.auth.TokenProvider;
import com.yoongu.security.apiserver.auth.common.JwtAuthenticationEntryPoint;
import com.yoongu.security.apiserver.auth.filter.AuthenticationFilter;
import com.yoongu.security.apiserver.auth.filter.JwtAuthenticationFilter;
import com.yoongu.security.apiserver.auth.handler.AuthenticationFailureHandlerImpl;
import com.yoongu.security.apiserver.auth.handler.AuthenticationSuccessHandlerImpl;
import com.yoongu.security.apiserver.auth.provider.AuthenticationProviderImpl;
import com.yoongu.security.apiserver.auth.provider.AuthenticationType;
import com.yoongu.security.apiserver.auth.provider.LdapAuthenticationProviderImpl;
import com.yoongu.security.apiserver.auth.service.UserService;
import com.yoongu.security.apiserver.common.SpringProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Configuration
    @Profile({SpringProfile.DEV, SpringProfile.STAGING, SpringProfile.UAT, SpringProfile.PROD})
    @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
    @EnableWebSecurity
    @RequiredArgsConstructor
    public static class TokenAuthSecurityConfig extends WebSecurityConfigurerAdapter {

        @Value("${authentication.type:JWT}")
        private String authenticationType;

        private final UserService userService;

        private final AccessLogService accessLogService;

        private final Cache<String, String> userCache;

        private final ObjectMapper objectMapper;

        private final TokenProvider tokenProvider;

        private final PasswordEncoder passwordEncoder;

        @Override
        public void configure(WebSecurity web) {
            web.ignoring().antMatchers("/configuration/**", "/v2/api-docs", "/configuration/ui", "/h2-console",
                    "/swagger-resources/**", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();

            http.httpBasic().disable()
                    .authorizeRequests()
                    .antMatchers("/api/**").permitAll()
                    .antMatchers("/admin/v1/auth/token/refresh").permitAll()
                    .antMatchers("/admin/**").authenticated()
                    // Every request is authentication and but filtered in jwt interceptor, Otherwise spring basic authentication will block requests.
                    .anyRequest().permitAll()
            ;

            http
                    .formLogin().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ;

            // Filter order is important
            http
                    .addFilter(authenticationFilter())
                    .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling()
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint())
            ;
        }

        @Override
        public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
            authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        }

        @Bean
        public AuthenticationFilter authenticationFilter() throws Exception {
            AuthenticationFilter authenticationFilter = new AuthenticationFilter(super.authenticationManager());
            authenticationFilter.setFilterProcessesUrl("/auth/login");
            authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
            authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
            authenticationFilter.afterPropertiesSet();
            return authenticationFilter;
        }

        @Bean
        public AuthenticationSuccessHandlerImpl authenticationSuccessHandler() {
            return new AuthenticationSuccessHandlerImpl(tokenProvider, userCache, accessLogService, objectMapper);
        }

        @Bean
        public AuthenticationFailureHandlerImpl authenticationFailureHandler() {
            return new AuthenticationFailureHandlerImpl(objectMapper, accessLogService, userService);
        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(userCache, tokenProvider);
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
            if (AuthenticationType.LDAP.name().equals(authenticationType)) {
                return new LdapAuthenticationProviderImpl(userService);
            }

            return new AuthenticationProviderImpl(userService, passwordEncoder);
        }

        @Bean
        public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
            return new JwtAuthenticationEntryPoint(objectMapper, accessLogService, userService);
        }


    }

    @Configuration
    @EnableWebSecurity
    @Profile({SpringProfile.TEST, SpringProfile.LOCAL})
    public static class DisableAuthSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) {
            web.ignoring().antMatchers("/configuration/**", "/v2/api-docs", "/configuration/ui",
                "/swagger-resources/**", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/h2-console");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .csrf().disable()
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .headers().frameOptions().disable();
        }
    }
}
