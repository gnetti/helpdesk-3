package com.luiz.helpdesk.application.ports.in;

import java.util.List;

public interface PaginationUseCasePort {
    int getMinPageSize();

    int getMaxPageSize();

    int getDefaultPage();

    int getDefaultSize();

    List<Integer> getAllowedPageSizes();

    String getDefaultSort();

    int getMaxTotalElements();

    String validateSort(String sort);

    boolean isValidPageSize(int pageSize);
}