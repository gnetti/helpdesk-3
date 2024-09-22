package com.luiz.helpdesk.domain.model;

import java.util.List;
import java.util.function.Function;

public record Pagination<T>(
        int pageNumber,
        int pageSize,
        List<T> content,
        long totalElements,
        int totalPages
) {
    public static <T> Pagination<T> of(int pageNumber, int pageSize, List<T> content, long totalElements, int totalPages) {
        return new Pagination<>(pageNumber, pageSize, content, totalElements, totalPages);
    }

    public <R> Pagination<R> map(Function<T, R> converter) {
        List<R> convertedContent = content.stream().map(converter).toList();
        return new Pagination<>(pageNumber, pageSize, convertedContent, totalElements, totalPages);
    }

    public static <T> Pagination<T> empty(int pageSize) {
        return new Pagination<>(0, pageSize, List.of(), 0, 0);
    }

    public boolean hasContent() {
        return !content.isEmpty();
    }

    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    public int getNumberOfElements() {
        return content.size();
    }
}