package com.luiz.helpdesk.domain.exception.addres;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(String message) {
        super(message);
    }
}
