package com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils;

import com.luiz.helpdesk.domain.model.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class PaginationUtil {

    private PaginationUtil() {
        throw new AssertionError("A classe utilitária não deve ser instanciada");
    }

    public static <T, R> Pagination<R> mapToPagination(
            int page,
            int size,
            List<T> content,
            long totalElements,
            int totalPages,
            Function<T, R> mapper
    ) {
        validatePaginationParams(page, size);
        List<R> mappedContent = mapContent(content, mapper);
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

    public static <T> Pagination<T> createPagination(Pageable pageable, List<T> content, long totalElements) {
        int totalPages = calculateTotalPages(pageable.getPageSize(), totalElements);
        return new Pagination<>(pageable.getPageNumber(), pageable.getPageSize(), content, totalElements, totalPages);
    }

    public static <T> Pagination<T> emptyPagination() {
        return new Pagination<>(0, 0, Collections.emptyList(), 0, 0);
    }

    private static void validatePaginationParams(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("O índice da página não deve ser menor que zero");
        }
        if (size < 1) {
            throw new IllegalArgumentException("O tamanho da página não deve ser menor que um");
        }
    }

    private static <T, R> List<R> mapContent(List<T> content, Function<T, R> mapper) {
        return content != null ? content.stream().map(mapper).toList() : Collections.emptyList();
    }

    private static int calculateTotalPages(int pageSize, long totalElements) {
        return pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) pageSize);
    }
}