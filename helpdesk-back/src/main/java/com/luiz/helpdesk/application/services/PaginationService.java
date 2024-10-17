package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.PaginationUseCasePort;
import com.luiz.helpdesk.domain.validator.PaginationValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginationService implements PaginationUseCasePort {

    private final PaginationValidator paginationValidator;

    public PaginationService(PaginationValidator paginationValidator) {
        this.paginationValidator = paginationValidator;
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

    @Override
    public boolean isValidPageSize(int pageSize) {
        return pageSize >= getMinPageSize() && pageSize <= getMaxPageSize() && getAllowedPageSizes().contains(pageSize);
    }
}