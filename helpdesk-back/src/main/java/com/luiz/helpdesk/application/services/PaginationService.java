package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.PaginationUseCasePort;
import com.luiz.helpdesk.domain.validator.PaginationValidator;
import org.springframework.stereotype.Service;

@Service
public class PaginationService implements PaginationUseCasePort {

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
}