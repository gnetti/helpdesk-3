package com.luiz.helpdesk.domain.exception.pagination;

public class InvalidPaginationException extends RuntimeException {
    public InvalidPaginationException(String message) {
        super(message);
    }
}