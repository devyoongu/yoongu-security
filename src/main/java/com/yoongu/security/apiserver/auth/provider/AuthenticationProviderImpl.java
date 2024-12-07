package com.yoongu.security.apiserver.auth.provider;

import com.yoongu.security.apiserver.auth.exception.UserExpiredException;
import com.yoongu.security.apiserver.auth.service.UserService;
import com.yoongu.security.persistence.auth.User;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
        String userName = authenticationToken.getName();
        String password = (String) authenticationToken.getCredentials();
        User user = userDetailsService.getUserByUserName(userName);

        validateUserStatus(user);

        if (passwordEncoder.matches(password, user.getPassword())) {
            userDetailsService.doLoginProcess(userName);
            return new UsernamePasswordAuthenticationToken(user, password, Collections.singleton(new SimpleGrantedAuthority(user.getRole().getAuthority())));
        }

        if (user.getLoginFailureCount() >= 9) {
            log.error("{} login fail more then 10. account will be locked", userName);
            userDetailsService.lockUserAccount(userName);
        } else {
            userDetailsService.increaseLoginFailureCount(userName);
        }

        throw new BadCredentialsException(user.getUserName() + " Invalid password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private void validateUserStatus(User user) {
        if (!user.isEnabled() || !user.isAccountNonLocked() || !user.isAccountNonExpired()) {
            throw new LockedException("account is Locked. please reset this account from admin");
        }

        // check last login date
        long limitDays = 90L;
        if (!ObjectUtils.isEmpty(user.getLastLoginDate()) && Duration.between(user.getLastLoginDate(), LocalDateTime.now()).toDays() >= limitDays) {
            throw new UserExpiredException(limitDays + " days after password change date");
        }

        // check login accept date
        if (user.getLoginAcceptedDate().compareTo(LocalDate.now()) < 0) {
            throw new UserExpiredException("expire user login accepted date");
        }
    }

}
