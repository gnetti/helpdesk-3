package com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils;

import com.luiz.helpdesk.domain.model.Pagination;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static <T, R> Pagination<R> mapToPagination(
            int page,
            int size,
            List<T> content,
            long totalElements,
            int totalPages,
            Function<T, R> mapper
    ) {
        List<R> mappedContent = content.stream().map(mapper).toList();
        return new Pagination<>(page, size, mappedContent, totalElements, totalPages);
    }

    public static <T, R> Pagination<R> mapPageToPagination(Page<T> page, Function<T, R> mapper) {
        return mapToPagination(
                page.getNumber(),
                page.getSize(),
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                mapper
        );
    }
}