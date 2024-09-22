package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.exception.person.InvalidPersonDataException;
import com.luiz.helpdesk.domain.exception.person.PersonAlreadyExistsException;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Address;
import com.luiz.helpdesk.domain.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PersonValidator {

    public static void validatePersonForCreation(Person person) {
        List<String> errors = new ArrayList<>();
        validatePersonNotNull(person, errors);
        if (person != null) {
            validatePersonHasAddress(person, errors);
            validatePersonData(person, errors);
        }
        throwIfErrors(errors);
    }

    public static void validatePersonForUpdate(Person person) {
        List<String> errors = new ArrayList<>();
        validatePersonNotNull(person, errors);
        if (person != null) {
            validatePersonIdNotNull(person, errors);
            validatePersonHasAddress(person, errors);
            validatePersonData(person, errors);
        }
        throwIfErrors(errors);
    }

    private static void validatePersonNotNull(Person person, List<String> errors) {
        if (person == null) {
            errors.add("Person cannot be null");
        }
    }

    private static void validatePersonIdNotNull(Person person, List<String> errors) {
        if (person.getId() == null) {
            errors.add("Person id cannot be null");
        }
    }

    private static void validatePersonHasAddress(Person person, List<String> errors) {
        if (person.getAddress() == null) {
            errors.add("Person must have an address");
        }
    }

    private static void validatePersonData(Person person, List<String> errors) {
        validateField(person.getName(), "Person name", errors);
        validateField(person.getCpf(), "Person CPF", errors);
        validateField(person.getEmail(), "Person email", errors);
        validateField(person.getPassword(), "Person password", errors);
        if (person.getAddress() != null) {
            validateAddressData(person.getAddress(), errors);
        }
    }

    private static void validateAddressData(Address address, List<String> errors) {
        validateField(address.getStreet(), "Address street", errors);
        validateField(address.getNeighborhood(), "Address neighborhood", errors);
        validateField(address.getCity(), "Address city", errors);
        validateField(address.getState(), "Address state", errors);
        validateField(address.getZipCode(), "Address zip code", errors);
        validateField(address.getNumber(), "Address number", errors);
    }

    private static void validateField(String field, String fieldName, List<String> errors) {
        if (field == null || field.trim().isEmpty()) {
            errors.add(fieldName + " cannot be null or empty");
        }
    }

    public static void validateId(Integer id) {
        if (id == null) {
            throw new InvalidPersonDataException("Id cannot be null");
        }
    }

    public static void validateCpf(String cpf) {
        validateField(cpf, "CPF", new ArrayList<>());
    }

    public static void validateEmail(String email) {
        validateField(email, "Email", new ArrayList<>());
    }

    public static void validatePersonDoesNotExist(PersonPersistenceOutputPort personRepository, Person person) {
        validateUniqueField(person.getCpf(), "CPF", () -> personRepository.existsByCpfAndIdNot(person.getCpf(), person.getId()));
        validateUniqueField(person.getEmail(), "email", () -> personRepository.existsByEmailAndIdNot(person.getEmail(), person.getId()));
    }

    public static void validateUniqueFields(PersonPersistenceOutputPort personRepository, Person newPerson, Person existingPerson) {
        if (!newPerson.getCpf().equals(existingPerson.getCpf())) {
            validateUniqueField(newPerson.getCpf(), "CPF", () -> personRepository.existsByCpfAndIdNot(newPerson.getCpf(), newPerson.getId()));
        }
        if (!newPerson.getEmail().equals(existingPerson.getEmail())) {
            validateUniqueField(newPerson.getEmail(), "email", () -> personRepository.existsByEmailAndIdNot(newPerson.getEmail(), newPerson.getId()));
        }
    }

    private static void validateUniqueField(String field, String fieldName, Supplier<Boolean> existsCheck) {
        if (existsCheck.get()) {
            throw new PersonAlreadyExistsException("Person with " + fieldName + " " + field + " already exists");
        }
    }

    private static void throwIfErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidPersonDataException(String.join("; ", errors));
        }
    }

    public static void validatePersonExists(PersonPersistenceOutputPort personRepository, Integer id) {
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException("Person not found with id: " + id);
        }
    }

}