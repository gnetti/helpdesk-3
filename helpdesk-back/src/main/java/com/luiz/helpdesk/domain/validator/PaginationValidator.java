package com.luiz.helpdesk.domain.validator;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PaginationValidator {

    @Value("${pagination.min-page-size}")
    private int configMinPageSize;

    @Value("${pagination.max-page-size}")
    private int configMaxPageSize;

    @Value("${pagination.default-page}")
    private int configDefaultPage;

    @Value("${pagination.default-size}")
    private int configDefaultSize;

    @Value("${pagination.allowed-page-sizes}")
    private String configAllowedPageSizes;

    @Value("${pagination.default-sort}")
    private String configDefaultSort;

    @Value("${pagination.max-total-elements}")
    private int configMaxTotalElements;

    private static int minPageSize;
    private static int maxPageSize;
    private static int defaultPage;
    private static int defaultSize;
    private static List<Integer> allowedPageSizes;
    private static String defaultSort;
    private static int maxTotalElements;

    @PostConstruct
    public void init() {
        minPageSize = configMinPageSize;
        maxPageSize = configMaxPageSize;
        defaultPage = configDefaultPage;
        defaultSize = configDefaultSize;
        allowedPageSizes = Arrays.stream(configAllowedPageSizes.split(","))
                .map(Integer::parseInt)
                .toList();
        defaultSort = configDefaultSort;
        maxTotalElements = configMaxTotalElements;
    }

    public int validatePageNumber(Integer page, List<String> errors) {
        int validatedPage = (page != null) ? page : defaultPage;
        if (validatedPage < 0) {
            errors.add("Page number cannot be less than zero");
        }
        return validatedPage;
    }

    public int validatePageSize(Integer size, List<String> errors) {
        int validatedSize = (size != null) ? size : defaultSize;
        if (validatedSize < minPageSize) {
            errors.add("Page size cannot be less than " + minPageSize);
        }
        if (validatedSize > maxPageSize) {
            errors.add("Page size cannot be greater than " + maxPageSize);
        }
        if (!allowedPageSizes.contains(validatedSize)) {
            errors.add("Page size must be one of the following: " + allowedPageSizes);
        }
        return validatedSize;
    }

    public String validateSort(String sort) {
        return (sort != null && !sort.isEmpty()) ? sort : defaultSort;
    }

    public static int getMinPageSize() {
        return minPageSize;
    }

    public static int getMaxPageSize() {
        return maxPageSize;
    }

    public static int getDefaultPage() {
        return defaultPage;
    }

    public static int getDefaultSize() {
        return defaultSize;
    }

    public static List<Integer> getAllowedPageSizes() {
        return allowedPageSizes;
    }

    public static String getDefaultSort() {
        return defaultSort;
    }

    public static int getMaxTotalElements() {
        return maxTotalElements;
    }

    public static void setDefaultPage(int newDefaultPage) {
        if (newDefaultPage < 0) {
            throw new IllegalArgumentException("Default page cannot be less than zero");
        }
        defaultPage = newDefaultPage;
    }

    public static void setDefaultSize(int newDefaultSize) {
        if (newDefaultSize < minPageSize || newDefaultSize > maxPageSize || !allowedPageSizes.contains(newDefaultSize)) {
            throw new IllegalArgumentException("Default size must be between " + minPageSize + " and " + maxPageSize + " and must be one of the allowed sizes: " + allowedPageSizes);
        }
        defaultSize = newDefaultSize;
    }

    public static void setDefaultSort(String newDefaultSort) {
        if (newDefaultSort == null || newDefaultSort.isEmpty()) {
            throw new IllegalArgumentException("Default sort cannot be null or empty");
        }
        defaultSort = newDefaultSort;
    }

    public static void setMaxTotalElements(int newMaxTotalElements) {
        if (newMaxTotalElements <= 0) {
            throw new IllegalArgumentException("Max total elements must be greater than zero");
        }
        maxTotalElements = newMaxTotalElements;
    }
}