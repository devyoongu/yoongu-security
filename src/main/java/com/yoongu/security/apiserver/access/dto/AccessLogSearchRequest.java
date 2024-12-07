package com.yoongu.security.apiserver.access.dto;

import com.yoongu.security.apiserver.common.enums.AccessLogSearchType;
import com.yoongu.security.apiserver.common.enums.TimeSortOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogSearchRequest {

    @NotNull
    private Long searchStartTimestamp;

    @NotNull
    private Long searchEndTimestamp;

    private TimeSortOrder timeSortOrder = TimeSortOrder.DESC;

    private AccessLogSearchType searchType = AccessLogSearchType.ALL;

    private String searchText;

}
