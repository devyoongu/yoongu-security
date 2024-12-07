package com.yoongu.security.apiserver.auth;

import com.yoongu.security.apiserver.auth.exception.UnauthorizedException;
import com.yoongu.security.persistence.auth.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
@Component
public class TokenProvider {

    @Value("${jwt.access-token.expiration:600000}")     //default 10 minute
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration:86400000}")   // default 24 hour
    private long refreshTokenExpiration;

    @Value("${jwt.secret_key}")
    private String secretKey;

    public String generateJwtToken(User user) {
        JwtBuilder builder = Jwts.builder()
            .setHeader(this.createHeader())
            .setSubject(String.valueOf(user.getId()))
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .claim("username", user.getUserName())
            .claim("role", user.getRole())
            .claim("passwordDueDate", this.getRemainingPasswordDueDate(user))
            .claim("isFirstLogin", ObjectUtils.isEmpty(user.getLastPasswordModifiedDate()))
            .signWith(SignatureAlgorithm.HS256, this.createSigningKey());
        return builder.compact();
    }

    public String generateRefreshToken(User user) {
        JwtBuilder builder = Jwts.builder()
            .setHeader(this.createHeader())
            .setSubject(String.valueOf(user.getId()))
            .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
            .claim("username", user.getUserName())
            .signWith(SignatureAlgorithm.HS256, this.createSigningKey());
        return builder.compact();
    }

    public void verifyToken(String token) {
        this.getClaimsFormToken(token);
    }

    public String getTokenFromAuthHeaderValue(String authorizationHeaderValue) {
        if (StringUtils.isBlank(authorizationHeaderValue)) {
            throw new UnauthorizedException("Authorization header is empty");
        }

        if (!authorizationHeaderValue.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token is not Bearer type");
        }

        return authorizationHeaderValue.split(" ")[1];
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        header.put("regDate", System.currentTimeMillis());
        return header;
    }

    private Long getRemainingPasswordDueDate(User user) {
        long limitPasswordDays = 90L;
        if (user.getLastPasswordModifiedDate() == null) {
            return limitPasswordDays;
        } else {
            long passedDays = Duration.between(user.getLastPasswordModifiedDate(), LocalDateTime.now()).toDays();
            return limitPasswordDays - passedDays;
        }
    }

    private Key createSigningKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public Claims getClaimsFormToken(String token) {
        return Jwts.parser()
            .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
            .parseClaimsJws(token)
            .getBody();
    }

    public String getUserNameFromToken(String token) {
        Claims claims = this.getClaimsFormToken(token);
        return (String) claims.get("username");
    }
}
