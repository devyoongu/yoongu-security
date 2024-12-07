package com.yoongu.security.persistence.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yoongu.security.apiserver.auth.dto.request.UserModificationRequest;
import com.yoongu.security.apiserver.common.converter.JsonNodeConverter;
import com.yoongu.security.persistence.BaseTimeEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column
    private String nickName;

    @Column(nullable = false)
    @ColumnDefault(value = "0")
    private Integer loginFailureCount;

    @Column
    private boolean isAccountNonExpired;

    @Column
    private boolean isAccountNonLocked;

    @Column
    private boolean isCredentialsNonExpired;

    @Column
    private boolean isEnabled;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private LocalDateTime lastLoginDate;

    @Column
    private LocalDateTime lastPasswordModifiedDate;

    @Column(nullable = false)
    private LocalDate loginAcceptedDate;

    @Column(columnDefinition = "json")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode lastTwelvePasswordHistory;

    @Getter
    @AllArgsConstructor
    public enum UserRole implements GrantedAuthority {

        ROLE_USER(Code.USER),
        ROLE_ADMIN(Code.ADMIN),
        ROLE_ELDERLY(Code.ELDERLY),
        ROLE_SURVEY(Code.SURVEY),
        ROLE_SKATING(Code.SKATING)
        ;

        private final String authority;

        @Override
        public String getAuthority() {
            return authority;
        }

        public static class Code {

            public static final String USER = "ROLE_USER";
            public static final String ADMIN = "ROLE_ADMIN";
            public static final String ELDERLY = "ROLE_ELDERLY";
            public static final String SURVEY = "ROLE_SURVEY";
            public static final String SKATING = "ROLE_SKATING";
        }
    }

    public void login() {
        this.loginFailureCount = 0;
        this.lastLoginDate = LocalDateTime.now();
    }

    public void increaseLoginFailureCount() {
        loginFailureCount++;
    }

    public void updateInfo(UserModificationRequest userModificationRequest) {
        this.nickName = userModificationRequest.getNickName();
        this.role = userModificationRequest.getRole();
        if (userModificationRequest.getLoginAcceptedDate() != null) {
            this.loginAcceptedDate = userModificationRequest.getLoginAcceptedDate();
        }
    }

    public void reset(String password) {
        this.loginFailureCount = 0;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
        changePassword(password);
    }

    public void changePassword(String password) {
        this.password = password;
        this.lastPasswordModifiedDate = LocalDateTime.now();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode passwordHistory = objectMapper.createObjectNode();
        passwordHistory.put("password", password);
        passwordHistory.put("setUpDate", LocalDate.now().toString());

        if(lastTwelvePasswordHistory == null || lastTwelvePasswordHistory.isEmpty()) {
            lastTwelvePasswordHistory = objectMapper.createArrayNode();
        }

        ((ArrayNode) lastTwelvePasswordHistory).add(passwordHistory);
    }

}
