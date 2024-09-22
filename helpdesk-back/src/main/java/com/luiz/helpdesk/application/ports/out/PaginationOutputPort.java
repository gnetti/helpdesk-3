package com.luiz.helpdesk.application.ports.out;

import com.luiz.helpdesk.domain.model.Pagination;

public interface PaginationOutputPort {
    Pagination<?> createPageRequest(int pageNumber, int pageSize, long totalElements, int totalPages);

    int getMinPageSize();

    int getMaxPageSize();

    int getDefaultPage();

    int getDefaultSize();
}