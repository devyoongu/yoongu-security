package com.yoongu.security.apiserver.auth.filter;

import com.google.common.cache.Cache;
import com.yoongu.security.apiserver.auth.TokenProvider;
import com.yoongu.security.apiserver.auth.exception.AnotherLoginException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Cache<String, String> userCache;

    private final TokenProvider tokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return servletPath.equals("/admin/v1/auth/token/refresh") || !servletPath.startsWith("/admin");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = tokenProvider.getTokenFromAuthHeaderValue(authHeaderValue);

        if (isValidToken(request, accessToken) && !isDuplicateLogin(request, accessToken)) {
            createAuthenticationToken(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(HttpServletRequest request, String accessToken) {
        try {
            tokenProvider.getClaimsFormToken(accessToken);
            return true;
        } catch (RuntimeException e) {
            request.setAttribute("exception", e);
        }

        return false;
    }

    private boolean isDuplicateLogin(HttpServletRequest request, String token) {
        String userName = tokenProvider.getUserNameFromToken(token);
        String cachedGuid = userCache.getIfPresent(userName + ":guid");
        String requestGuid = request.getHeader("x-request-guid");

        if (StringUtils.isNotBlank(cachedGuid) && !cachedGuid.equals(requestGuid)) {
            log.debug("cached guid : {}, request guid : {}", cachedGuid, requestGuid);
            request.setAttribute("exception", new AnotherLoginException("Another User Login"));
            return true;
        }

        return false;
    }

    private void createAuthenticationToken(String token) {
        Claims claims = tokenProvider.getClaimsFormToken(token);
        String role = claims.get("role", String.class);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(claims.get("username"), null, Collections.singleton(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
