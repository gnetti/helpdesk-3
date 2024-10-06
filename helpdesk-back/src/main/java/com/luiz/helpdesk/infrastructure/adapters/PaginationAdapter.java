package com.luiz.helpdesk.infrastructure.adapters;

import com.luiz.helpdesk.application.ports.out.PaginationOutputPort;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.validator.PaginationValidator;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PaginationAdapter implements PaginationOutputPort {

    @Override
    public Pagination<?> createPageRequest(int pageNumber, int pageSize, long totalElements, int totalPages) {
        pageNumber = Math.max(pageNumber, PaginationValidator.getDefaultPage());
        pageSize = Math.max(Math.min(pageSize, PaginationValidator.getMaxPageSize()), PaginationValidator.getMinPageSize());

        return Pagination.of(pageNumber, pageSize, Collections.emptyList(), totalElements, totalPages);
    }

    public int getMinPageSize() {
        return PaginationValidator.getMinPageSize();
    }

    public int getMaxPageSize() {
        return PaginationValidator.getMaxPageSize();
    }

    public int getDefaultPage() {
        return PaginationValidator.getDefaultPage();
    }

    public int getDefaultSize() {
        return PaginationValidator.getDefaultSize();
    }
}