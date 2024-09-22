package com.luiz.helpdesk.application.ports.in;

public interface PaginationUseCasePort {
    int getMinPageSize();

    int getMaxPageSize();

    int getDefaultPage();

    int getDefaultSize();

}