package com.yoongu.security.apiserver.access.dto;

import com.yoongu.security.apiserver.common.enums.TimeSortOrder;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogSearchCondition {

    private String userName;

    @NotNull
    private Long searchStartTimestamp;

    @NotNull
    private Long searchEndTimestamp;

    private TimeSortOrder timeSortOrder = TimeSortOrder.DESC;

}
