package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.luiz.helpdesk.application.ports.in.PaginationUseCasePort;
import com.luiz.helpdesk.domain.model.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(description = "Data Transfer Object for Pagination",
        example = "{"
                + "\"pageNumber\":0,"
                + "\"pageSize\":20,"
                + "\"content\":[{\"id\":1,\"name\":\"Item 1\"},{\"id\":2,\"name\":\"Item 2\"}],"
                + "\"totalElements\":100,"
                + "\"totalPages\":5,"
                + "\"minPageSize\":10,"
                + "\"maxPageSize\":100,"
                + "\"defaultPage\":0,"
                + "\"defaultSize\":20"
                + "}")

public class PaginationDTO<T> {

    @Schema(description = "Page number (0-based)", example = "0")
    @Min(value = 0, message = "Page number must not be less than zero")
    private int pageNumber;

    @Schema(description = "Number of items per page", example = "20")
    private int pageSize;

    @Schema(description = "List of items in the current page")
    private List<T> content;

    @Schema(description = "Total number of items across all pages", example = "100")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;

    @Schema(description = "Minimum allowed page size", example = "10")
    private int minPageSize;

    @Schema(description = "Maximum allowed page size", example = "100")
    private int maxPageSize;

    @Schema(description = "Default page number", example = "0")
    private int defaultPage;

    @Schema(description = "Default page size", example = "20")
    private int defaultSize;

    private PaginationDTO() {
    }

    @Schema(description = "Builder for PaginationDTO")
    public static class Builder<T> {
        private final PaginationDTO<T> paginationDTO;

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

    @Schema(description = "Create PaginationDTO from domain Pagination")
    public static <T> PaginationDTO<T> fromDomainPagination(Pagination<T> pagination) {
        return new Builder<T>()
                .pageNumber(pagination.pageNumber())
                .pageSize(pagination.pageSize())
                .content(pagination.content())
                .totalElements(pagination.totalElements())
                .totalPages(pagination.totalPages())
                .build();
    }

    @Schema(description = "Create configuration PaginationDTO")
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

    @Schema(description = "Map content to a different type")
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

    @Schema(description = "Convert to domain Pagination")
    public Pagination<T> toDomainPagination() {
        return new Pagination<>(pageNumber, pageSize, content, totalElements, totalPages);
    }

    @Schema(description = "Validate and set page number")
    public void validateAndSetPageNumber(Integer page) {
        this.pageNumber = (page != null) ? page : this.defaultPage;
        if (this.pageNumber < 0) {
            throw new IllegalArgumentException("Page number must not be less than zero");
        }
    }

    @Schema(description = "Validate and set page size")
    public void validateAndSetPageSize(Integer size) {
        this.pageSize = (size != null) ? size : this.defaultSize;
        if (this.pageSize < this.minPageSize || this.pageSize > this.maxPageSize) {
            throw new IllegalArgumentException("Page size must be between " +
                    this.minPageSize + " and " + this.maxPageSize);
        }
    }

    @Schema(description = "Copy configuration to another PaginationDTO")
    public void copyConfigTo(PaginationDTO<?> other) {
        other.minPageSize = this.minPageSize;
        other.maxPageSize = this.maxPageSize;
        other.defaultPage = this.defaultPage;
        other.defaultSize = this.defaultSize;
    }

    @Schema(description = "Get the page number")
    public int getPageNumber() {
        return pageNumber;
    }

    @Schema(description = "Get the page size")
    public int getPageSize() {
        return pageSize;
    }

    @Schema(description = "Get the content of the current page")
    public List<T> getContent() {
        return content;
    }

    @Schema(description = "Get the total number of elements")
    public long getTotalElements() {
        return totalElements;
    }

    @Schema(description = "Get the total number of pages")
    public int getTotalPages() {
        return totalPages;
    }

    @Schema(description = "Get the minimum page size")
    public int getMinPageSize() {
        return minPageSize;
    }

    @Schema(description = "Get the maximum page size")
    public int getMaxPageSize() {
        return maxPageSize;
    }

    @Schema(description = "Get the default page number")
    public int getDefaultPage() {
        return defaultPage;
    }

    @Schema(description = "Get the default page size")
    public int getDefaultSize() {
        return defaultSize;
    }

    @Schema(description = "Check if the page has content")
    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    @Schema(description = "Check if there is a next page")
    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    @Schema(description = "Check if there is a previous page")
    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    @Schema(description = "Get the number of elements in the current page")
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
        return Objects.hash(pageNumber,
                pageSize, content,
                totalElements,
                totalPages, minPageSize,
                maxPageSize, defaultPage,
                defaultSize);
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