package com.yoongu.security.apiserver.common.pagination;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResponse<T> {

    private List<T> contents = new ArrayList<>();

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;
}
