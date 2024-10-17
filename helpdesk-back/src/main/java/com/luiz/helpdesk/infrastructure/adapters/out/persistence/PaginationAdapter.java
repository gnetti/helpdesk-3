package com.luiz.helpdesk.infrastructure.adapters.out.persistence;

import com.luiz.helpdesk.application.ports.out.PaginationOutputPort;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.validator.PaginationValidator;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PaginationAdapter implements PaginationOutputPort {

    private final PaginationValidator paginationValidator;

    public PaginationAdapter(PaginationValidator paginationValidator) {
        this.paginationValidator = paginationValidator;
    }

    @Override
    public <T> Pagination<T> createPageRequest(int pageNumber, int pageSize, long totalElements, int totalPages) {
        int validatedPageNumber = validatePageNumber(pageNumber);
        int validatedPageSize = validatePageSize(pageSize);
        long validatedTotalElements = validateTotalElements(totalElements);
        int validatedTotalPages = validateTotalPages(totalPages, validatedPageSize, validatedTotalElements);

        return Pagination.of(validatedPageNumber, validatedPageSize, Collections.emptyList(), validatedTotalElements, validatedTotalPages);
    }

    @Override
    public int getMinPageSize() {
        return PaginationValidator.getMinPageSize();
    }

    @Override
    public int getMaxPageSize() {
        return PaginationValidator.getMaxPageSize();
    }

    @Override
    public int getDefaultPage() {
        return PaginationValidator.getDefaultPage();
    }

    @Override
    public int getDefaultSize() {
        return PaginationValidator.getDefaultSize();
    }

    @Override
    public List<Integer> getAllowedPageSizes() {
        return PaginationValidator.getAllowedPageSizes();
    }

    @Override
    public String getDefaultSort() {
        return PaginationValidator.getDefaultSort();
    }

    @Override
    public int getMaxTotalElements() {
        return PaginationValidator.getMaxTotalElements();
    }

    @Override
    public String validateSort(String sort) {
        return paginationValidator.validateSort(sort);
    }

    private int validatePageNumber(int pageNumber) {
        return Math.max(pageNumber, getDefaultPage());
    }

    private int validatePageSize(int pageSize) {
        if (!isValidPageSize(pageSize)) {
            return getDefaultSize();
        }
        return pageSize;
    }

    private long validateTotalElements(long totalElements) {
        return Math.min(Math.max(totalElements, 0), getMaxTotalElements());
    }

    private int validateTotalPages(int totalPages, int pageSize, long totalElements) {
        int calculatedTotalPages = (int) Math.ceil((double) totalElements / pageSize);
        return Math.max(totalPages, calculatedTotalPages);
    }
}