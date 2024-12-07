package com.yoongu.security.persistence.access;

import com.yoongu.security.persistence.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
public class AccessLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String userIp;

    @Column(nullable = false)
    private String requestMethod;

    @Column(nullable = false)
    private String requestUrl;

    @Column(nullable = false)
    private Integer httpStatusCode;

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

}
