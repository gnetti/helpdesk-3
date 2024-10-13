package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.domain.exception.addres.InvalidAddressDataException;
import com.luiz.helpdesk.domain.model.Address;

public class AddressValidator {

    public static void validateAddress(Address address) {
        if (address == null) {
            throw new InvalidAddressDataException("O endereço não pode ser nulo");
        }
    }

    public static void validateAddressId(Integer id) {
        if (id == null) {
            throw new InvalidAddressDataException("O ID do endereço não pode ser nulo");
        }
    }

    public static void validatePersonId(Integer personId) {
        if (personId == null) {
            throw new InvalidAddressDataException("O ID da pessoa não pode ser nulo");
        }
    }

    public static void validateZipCode(String zipCode) {
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new InvalidAddressDataException("O CEP não pode ser nulo ou vazio");
        }
    }

    public static void validateAddressForUpdate(Address address) {
        validateAddress(address);
        validateAddressId(address.getId());
    }
}