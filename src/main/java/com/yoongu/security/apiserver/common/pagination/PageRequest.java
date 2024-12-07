package com.yoongu.security.apiserver.common.pagination;

public class PageRequest {

    private static final int DEFAULT_PAGE_SIZE = 5;

    private static final int MAX_PAGE_SIZE = 100;

    private int page;

    private int pageSize;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public int getPage() {
        return page <= 0 ? 1 : page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = (pageSize <= 0 || pageSize > MAX_PAGE_SIZE) ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public int getPageSize() {
        return (pageSize <= 0 || pageSize > MAX_PAGE_SIZE) ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(getPage() - 1, getPageSize());
    }
}
