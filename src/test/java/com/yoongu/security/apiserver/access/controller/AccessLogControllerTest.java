package com.yoongu.security.apiserver.access.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.base.Charsets;
import com.yoongu.security.apiserver.access.dto.AccessLogDto;
import com.yoongu.security.apiserver.access.dto.AccessLogSearchCondition;
import com.yoongu.security.apiserver.access.service.AccessLogService;
import com.yoongu.security.apiserver.common.pagination.PageResponse;
import com.yoongu.security.apiserver.common.SpringProfile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
    value = AccessLogController.class,
    useDefaultFilters = false,
    includeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = AccessLogController.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(SpringProfile.TEST)
public class AccessLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessLogService accessLogService;

    @Test
    @DisplayName("검색조건에 맞는 Access Log를 가져온다.")
    public void getAccessLogTest() throws Exception {
        // given
        String userName = "유안상";
        String searchStartTimeStamp = "0";
        String searchEndTimeStamp = "1614922333";
        when(this.accessLogService.getBySearchCondition(any(Pageable.class), any(AccessLogSearchCondition.class))).thenReturn(this.createAccessLogs());

        // when
        ResultActions resultActions = mockMvc.perform(get("/admin/v1/access/logs")
            .param("page", "0")
            .param("pageSize", "10")
            .param("userName", userName)
            .param("searchStartTimestamp", searchStartTimeStamp)
            .param("searchEndTimestamp", searchEndTimeStamp)
            .characterEncoding(Charsets.UTF_8.name())
        );

        // then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.contents", hasSize(10)))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.pageSize").value(10))
            .andExpect(jsonPath("$.totalPages").value(10));
    }

    private PageResponse<AccessLogDto> createAccessLogs() {
        List<AccessLogDto> accessLogs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AccessLogDto accessLogDto = AccessLogDto.builder()
                .userName("유안상")
                .userIp("127.0.0.1")
                .requestUrl("/admin/v1/test")
                .requestMethod("POST")
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
            accessLogs.add(accessLogDto);
        }
        return new PageResponse<>(accessLogs, 1, 10, 10);
    }

    @Test
    @DisplayName("검색조건에 검증에 실패한다. (searchStartTimeStamp)")
    public void getAccessLogTestFailure() throws Exception {
        // given
        String userName = "";
        String searchEndTimestamp = "1614800811";

        // when
        ResultActions resultActions = mockMvc.perform(get("/admin/v1/access/logs")
            .param("userName", userName)
            .param("searchEndTimestamp", searchEndTimestamp)
            .characterEncoding(Charsets.UTF_8.name())
        );

        // then
        resultActions.andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("검색조건에 검증에 실패한다. (searchEndTimeStamp)")
    public void getAccessLogTestFailure2() throws Exception {
        // given
        String userName = "";
        String searchStartTimestamp = "1614800811";

        // when
        ResultActions resultActions = mockMvc.perform(get("/admin/v1/access/logs")
            .param("userName", userName)
            .param("searchStartTimestamp", searchStartTimestamp)
            .characterEncoding(Charsets.UTF_8.name())
        );

        // then
        resultActions.andDo(print())
            .andExpect(status().isBadRequest());
    }
}