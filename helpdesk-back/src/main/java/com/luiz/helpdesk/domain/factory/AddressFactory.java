package com.luiz.helpdesk.domain.factory;

import com.luiz.helpdesk.domain.model.Address;

public interface AddressFactory {
    Address createAddress(String street, String complement, String neighborhood, String city, String state, String zipCode, String number);
}
