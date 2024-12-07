package com.yoongu.security.apiserver.auth.dto;

import com.yoongu.security.apiserver.common.enums.TimeSortOrder;
import com.yoongu.security.apiserver.common.enums.UserSearchType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchCondition {

    private UserSearchType searchType = UserSearchType.ALL;

    private String searchText;

    private TimeSortOrder timeSortOrder = TimeSortOrder.DESC;

}
