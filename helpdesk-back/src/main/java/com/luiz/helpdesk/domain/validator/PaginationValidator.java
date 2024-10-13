package com.luiz.helpdesk.domain.validator;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private static int minPageSize;
    private static int maxPageSize;
    private static int defaultPage;
    private static int defaultSize;

    @PostConstruct
    public void init() {
        minPageSize = configMinPageSize;
        maxPageSize = configMaxPageSize;
        defaultPage = configDefaultPage;
        defaultSize = configDefaultSize;
    }

    public int validatePageNumber(Integer page, List<String> errors) {
        int validatedPage = (page != null) ? page : defaultPage;
        if (validatedPage < 0) {
            errors.add("O número da página não pode ser menor que zero");
        }
        return validatedPage;
    }

    public int validatePageSize(Integer size, List<String> errors) {
        int validatedSize = (size != null) ? size : defaultSize;
        if (validatedSize < minPageSize) {
            errors.add("O tamanho da página não pode ser menor que " + minPageSize);
        }
        if (validatedSize > maxPageSize) {
            errors.add("O tamanho da página não pode ser maior que " + maxPageSize);
        }
        return validatedSize;
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

    public static void incrementMinPageSize(int increment) {
        minPageSize += increment;
    }

    public static void incrementMaxPageSize(int increment) {
        maxPageSize += increment;
    }

    public static void setDefaultPage(int newDefaultPage) {
        if (newDefaultPage < 0) {
            throw new IllegalArgumentException("A página padrão não pode ser menor que zero");
        }
        defaultPage = newDefaultPage;
    }

    public static void setDefaultSize(int newDefaultSize) {
        if (newDefaultSize < minPageSize || newDefaultSize > maxPageSize) {
            throw new IllegalArgumentException("O tamanho padrão deve estar entre " + minPageSize + " e " + maxPageSize);
        }
        defaultSize = newDefaultSize;
    }
}