package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.domain.exception.addres.InvalidAddressDataException;
import com.luiz.helpdesk.domain.model.Address;

public class AddressValidator {

    public static void validateAddress(Address address) {
        if (address == null) {
            throw new InvalidAddressDataException("Address cannot be null");
        }
    }

    public static void validateAddressId(Integer id) {
        if (id == null) {
            throw new InvalidAddressDataException("Address ID cannot be null");
        }
    }

    public static void validatePersonId(Integer personId) {
        if (personId == null) {
            throw new InvalidAddressDataException("Person ID cannot be null");
        }
    }

    public static void validateZipCode(String zipCode) {
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new InvalidAddressDataException("Zip code cannot be null or empty");
        }
    }

    public static void validateAddressForUpdate(Address address) {
        validateAddress(address);
        validateAddressId(address.getId());
    }
}
