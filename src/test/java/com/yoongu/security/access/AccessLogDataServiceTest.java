package com.yoongu.security.access;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.yoongu.security.apiserver.access.dto.AccessLogSearchCondition;
import com.yoongu.security.persistence.access.AccessLog;
import com.yoongu.security.persistence.access.AccessLogDataService;
import com.yoongu.security.persistence.access.AccessLogRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class AccessLogDataServiceTest {

    @InjectMocks
    private AccessLogDataService accessLogDataService;

    @Mock
    private AccessLogRepository accessLogRepository;

    @Test
    @DisplayName("AccessLog 저장을 확인한다.")
    public void saveTest() {
        // given
        String userName = "유안상";
        String userIp = "127.0.0.1";
        String requestMethod = "POST";
        String requestUrl = "/admin/v1/test";
        int httpStatusCode = 201;

        AccessLog accessLog = AccessLog.builder()
            .userName(userName)
            .userIp(userIp)
            .requestMethod(requestMethod)
            .requestUrl(requestUrl)
            .httpStatusCode(httpStatusCode)
            .build();

        AccessLog willSavedAccessLog = AccessLog.builder()
            .id(1L)
            .userName(userName)
            .userIp(userIp)
            .requestMethod(requestMethod)
            .requestUrl(requestUrl)
            .httpStatusCode(httpStatusCode)
            .build();

        given(this.accessLogRepository.save(accessLog)).willReturn(willSavedAccessLog);

        // when
        AccessLog savedAccessLog = this.accessLogDataService.save(accessLog);

        // then
        assertThat(savedAccessLog).isNotNull();
        assertThat(savedAccessLog.getId()).isEqualTo(1L);
        assertThat(savedAccessLog.getUserName()).isEqualTo(userName);
        assertThat(savedAccessLog.getUserIp()).isEqualTo(userIp);
        assertThat(savedAccessLog.getRequestMethod()).isEqualTo(requestMethod);
        assertThat(savedAccessLog.getRequestUrl()).isEqualTo(requestUrl);
        assertThat(savedAccessLog.getHttpStatusCode()).isEqualTo(httpStatusCode);
    }


    @Test
    @DisplayName("검색조건으로 AccessLog 리스트 가져온다.")
    public void getAccessLogBySearchConditionsTest() {
        // given
        int page = 0;
        int pageSize = 10;
        String userName = "유안상";
        long searchStartTimestamp = 0L;
        long searchEndTimestamp = 1614811086L;

        AccessLogSearchCondition accessLogSearchCondition = new AccessLogSearchCondition();
        accessLogSearchCondition.setUserName(userName);
        accessLogSearchCondition.setSearchStartTimestamp(searchStartTimestamp);
        accessLogSearchCondition.setSearchEndTimestamp(searchEndTimestamp);

        Page<AccessLog> accessLogPage = this.createAccessLogPage();
        Pageable pageable = PageRequest.of(page, pageSize);

        given(this.accessLogRepository.findBySearchCondition(pageable, accessLogSearchCondition)).willReturn(accessLogPage);

        // when
        Page<AccessLog> returnedAccessLogPage = this.accessLogDataService.getBySearchCondition(pageable, accessLogSearchCondition);

        // then
        assertThat(returnedAccessLogPage.getContent()).isNotNull();
        assertThat(returnedAccessLogPage.getContent()).isNotEmpty();
        assertThat(returnedAccessLogPage.getContent().size()).isEqualTo(10);
    }

    private PageImpl<AccessLog> createAccessLogPage() {
        List<AccessLog> accessLogs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AccessLog accessLog = AccessLog.builder()
                .id(i + 1L)
                .userName("유안상")
                .userIp("127.0.0.1")
                .requestUrl("/admin/v1/test")
                .requestMethod("POST")
                .build();
            accessLogs.add(accessLog);
        }
        return new PageImpl<>(accessLogs);
    }
}