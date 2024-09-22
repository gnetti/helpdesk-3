package com.luiz.helpdesk.domain.factory;

import com.luiz.helpdesk.domain.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressFactoryImpl implements AddressFactory {

    @Override
    public Address createAddress(String street, String complement, String neighborhood, String city, String state, String zipCode, String number) {
        return Address.createNew(street, complement, neighborhood, city, state, zipCode, number, null);
    }
}
