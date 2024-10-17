package com.luiz.helpdesk.application.ports.out;

import com.luiz.helpdesk.domain.model.Pagination;

import java.util.List;

public interface PaginationOutputPort {

    <T> Pagination<T> createPageRequest(int pageNumber, int pageSize, long totalElements, int totalPages);

    int getMinPageSize();

    int getMaxPageSize();

    int getDefaultPage();

    int getDefaultSize();

    List<Integer> getAllowedPageSizes();

    String getDefaultSort();

    int getMaxTotalElements();

    String validateSort(String sort);

    default <T> Pagination<T> createEmptyPage() {
        return createPageRequest(getDefaultPage(), getDefaultSize(), 0, 0);
    }

    default boolean isValidPageSize(int pageSize) {
        return pageSize >= getMinPageSize() && pageSize <= getMaxPageSize() && getAllowedPageSizes().contains(pageSize);
    }
}