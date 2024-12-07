package com.yoongu.security.apiserver.auth.service;

import com.google.common.cache.Cache;
import com.yoongu.security.apiserver.auth.TokenProvider;
import com.yoongu.security.apiserver.auth.dto.response.TokenDto;
import com.yoongu.security.persistence.auth.User;
import com.yoongu.security.persistence.auth.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;

    private final UserDataService userDataService;

    private final Cache<String, String> userCache;

    public TokenDto reissueToken(String authHeaderValue) {
        String refreshToken = tokenProvider.getTokenFromAuthHeaderValue(authHeaderValue);

        tokenProvider.verifyToken(refreshToken);

        String userName = tokenProvider.getUserNameFromToken(refreshToken);
        User user = userDataService.getByUserName(userName);
        String newAccessToken = tokenProvider.generateJwtToken(user);

        userCache.put(userName, newAccessToken);

        return TokenDto.builder()
            .refreshToken(refreshToken)
            .accessToken(newAccessToken)
            .build();
    }
}
