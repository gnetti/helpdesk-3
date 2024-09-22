package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.luiz.helpdesk.application.ports.in.PaginationUseCasePort;
import com.luiz.helpdesk.domain.model.Pagination;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginationDTO<T> {

    @Min(value = 0, message = "Page number must not be less than zero")
    private int pageNumber;
    private int pageSize;
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int minPageSize;
    private int maxPageSize;
    private int defaultPage;
    private int defaultSize;

    private PaginationDTO() {
    }

    public static class Builder<T> {
        private PaginationDTO<T> paginationDTO;

        public Builder() {
            paginationDTO = new PaginationDTO<>();
        }

        public Builder<T> pageNumber(int pageNumber) {
            paginationDTO.pageNumber = pageNumber;
            return this;
        }

        public Builder<T> pageSize(int pageSize) {
            paginationDTO.pageSize = pageSize;
            return this;
        }

        public Builder<T> content(List<T> content) {
            paginationDTO.content = content;
            return this;
        }

        public Builder<T> totalElements(long totalElements) {
            paginationDTO.totalElements = totalElements;
            return this;
        }

        public Builder<T> totalPages(int totalPages) {
            paginationDTO.totalPages = totalPages;
            return this;
        }

        public Builder<T> minPageSize(int minPageSize) {
            paginationDTO.minPageSize = minPageSize;
            return this;
        }

        public Builder<T> maxPageSize(int maxPageSize) {
            paginationDTO.maxPageSize = maxPageSize;
            return this;
        }

        public Builder<T> defaultPage(int defaultPage) {
            paginationDTO.defaultPage = defaultPage;
            return this;
        }

        public Builder<T> defaultSize(int defaultSize) {
            paginationDTO.defaultSize = defaultSize;
            return this;
        }

        public PaginationDTO<T> build() {
            return paginationDTO;
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> PaginationDTO<T> fromDomainPagination(Pagination<T> pagination) {
        return new Builder<T>()
                .pageNumber(pagination.pageNumber())
                .pageSize(pagination.pageSize())
                .content(pagination.content())
                .totalElements(pagination.totalElements())
                .totalPages(pagination.totalPages())
                .build();
    }

    public static <T> PaginationDTO<T> createConfigDTO(PaginationUseCasePort paginationUseCase) {
        return new Builder<T>()
                .minPageSize(paginationUseCase.getMinPageSize())
                .maxPageSize(paginationUseCase.getMaxPageSize())
                .defaultPage(paginationUseCase.getDefaultPage())
                .defaultSize(paginationUseCase.getDefaultSize())
                .pageNumber(paginationUseCase.getDefaultPage())
                .pageSize(paginationUseCase.getDefaultSize())
                .build();
    }

    public <R> PaginationDTO<R> map(Function<T, R> converter) {
        List<R> convertedContent = this.content.stream().map(converter).collect(Collectors.toList());
        return new Builder<R>()
                .pageNumber(this.pageNumber)
                .pageSize(this.pageSize)
                .content(convertedContent)
                .totalElements(this.totalElements)
                .totalPages(this.totalPages)
                .minPageSize(this.minPageSize)
                .maxPageSize(this.maxPageSize)
                .defaultPage(this.defaultPage)
                .defaultSize(this.defaultSize)
                .build();
    }

    public Pagination<T> toDomainPagination() {
        return new Pagination<>(pageNumber, pageSize, content, totalElements, totalPages);
    }

    public void validateAndSetPageNumber(Integer page) {
        this.pageNumber = (page != null) ? page : this.defaultPage;
        if (this.pageNumber < 0) {
            throw new IllegalArgumentException("Page number must not be less than zero");
        }
    }

    public void validateAndSetPageSize(Integer size) {
        this.pageSize = (size != null) ? size : this.defaultSize;
        if (this.pageSize < this.minPageSize || this.pageSize > this.maxPageSize) {
            throw new IllegalArgumentException("Page size must be between " +
                    this.minPageSize + " and " + this.maxPageSize);
        }
    }

    public void copyConfigTo(PaginationDTO<?> other) {
        other.minPageSize = this.minPageSize;
        other.maxPageSize = this.maxPageSize;
        other.defaultPage = this.defaultPage;
        other.defaultSize = this.defaultSize;
    }

    // Getters

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getMinPageSize() {
        return minPageSize;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public int getDefaultPage() {
        return defaultPage;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    public int getNumberOfElements() {
        return content != null ? content.size() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaginationDTO<?> that = (PaginationDTO<?>) o;
        return pageNumber == that.pageNumber &&
                pageSize == that.pageSize &&
                totalElements == that.totalElements &&
                totalPages == that.totalPages &&
                minPageSize == that.minPageSize &&
                maxPageSize == that.maxPageSize &&
                defaultPage == that.defaultPage &&
                defaultSize == that.defaultSize &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize, content, totalElements, totalPages, minPageSize, maxPageSize, defaultPage, defaultSize);
    }

    @Override
    public String toString() {
        return "PaginationDTO{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", content=" + content +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", minPageSize=" + minPageSize +
                ", maxPageSize=" + maxPageSize +
                ", defaultPage=" + defaultPage +
                ", defaultSize=" + defaultSize +
                '}';
    }
}