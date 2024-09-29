package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.exception.person.InvalidPasswordException;
import com.luiz.helpdesk.domain.exception.person.InvalidPersonDataException;
import com.luiz.helpdesk.domain.exception.person.PersonAlreadyExistsException;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Address;
import com.luiz.helpdesk.domain.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PersonValidator {

    public static void validateForCreation(Person person, PersonPersistenceOutputPort repository) {
        List<String> errors = new ArrayList<>();
        validateBasicPersonData(person, errors);
        validatePasswordNotEmpty(person.getPassword(), errors);
        validateUniqueFields(repository, person, null, errors);
        throwIfErrors(errors);
    }

    public static void validateForUpdate(Person person, PersonPersistenceOutputPort repository, Person existingPerson) {
        List<String> errors = new ArrayList<>();
        validateBasicPersonData(person, errors);
        validateUniqueFields(repository, person, existingPerson, errors);
        throwIfErrors(errors);
    }

    public static void validateForCurrentUserUpdate(Person updatedPerson, String currentPassword, String newPassword, PersonPersistenceOutputPort repository, Person existingPerson) {
        List<String> errors = new ArrayList<>();
        validateBasicPersonDataForCurrentUser(updatedPerson, errors);
        validateCurrentPasswordProvided(currentPassword, errors);
        validateNewPasswordIfProvided(newPassword, errors);
        validateUniqueFieldsForCurrentUser(repository, updatedPerson, existingPerson, errors);
        throwIfErrors(errors);
    }

    private static void validateBasicPersonData(Person person, List<String> errors) {
        if (person == null) {
            errors.add("Person cannot be null");
            return;
        }
        validateField(person.getName(), "Person name", errors);
        validateField(person.getCpf(), "Person CPF", errors);
        validateField(person.getEmail(), "Person email", errors);
        validateAddress(person.getAddress(), errors);
    }

    private static void validateAddress(Address address, List<String> errors) {
        if (address == null) {
            errors.add("Person must have an address");
            return;
        }
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

    private static void validatePasswordNotEmpty(String password, List<String> errors) {
        validateField(password, "Password", errors);
    }

    private static void validateCurrentPassword(String encryptedCurrentPassword, List<String> errors) {
        validateField(encryptedCurrentPassword, "Current password", errors);
    }

    private static void validateNewPasswordIfProvided(String newPassword, List<String> errors) {
        if (newPassword != null) {
            if (newPassword.trim().isEmpty()) {
                errors.add("New password cannot be empty if provided");
            } else if (newPassword.length() < 8) {
                errors.add("New password must be at least 8 characters long");
            }
        }
    }

    private static void validateUniqueFields(PersonPersistenceOutputPort repository, Person newPerson, Person existingPerson, List<String> errors) {
        if (existingPerson == null || !newPerson.getCpf().equals(existingPerson.getCpf())) {
            validateUniqueField(newPerson.getCpf(), "CPF", () -> repository.existsByCpfAndIdNot(newPerson.getCpf(), newPerson.getId()), errors);
        }
        if (existingPerson == null || !newPerson.getEmail().equals(existingPerson.getEmail())) {
            validateUniqueField(newPerson.getEmail(), "email", () -> repository.existsByEmailAndIdNot(newPerson.getEmail(), newPerson.getId()), errors);
        }
    }

    private static void validateUniqueFieldsForCurrentUser(PersonPersistenceOutputPort repository, Person updatedPerson, Person existingPerson, List<String> errors) {
        if (!updatedPerson.getEmail().equals(existingPerson.getEmail())) {
            errors.add("Email cannot be changed for current user update");
        }
        if (!updatedPerson.getCpf().equals(existingPerson.getCpf())) {
            errors.add("CPF cannot be changed for current user update");
        }
    }

    private static void validateUniqueField(String field, String fieldName, Supplier<Boolean> existsCheck, List<String> errors) {
        if (existsCheck.get()) {
            errors.add("Person with " + fieldName + " " + field + " already exists");
        }
    }

    private static void throwIfErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidPersonDataException(String.join("; ", errors));
        }
    }

    public static void validateId(Integer id) {
        if (id == null) {
            throw new InvalidPersonDataException("Id cannot be null");
        }
    }

    public static void validateCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new InvalidPersonDataException("CPF cannot be null or empty");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidPersonDataException("Email cannot be null or empty");
        }
    }

    public static void validatePersonExists(PersonPersistenceOutputPort repository, Integer id) {
        if (!repository.existsById(id)) {
            throw new PersonNotFoundException("Person not found with id: " + id);
        }
    }

    public static Person validateAndGetPersonByEmail(PersonPersistenceOutputPort repository, String email) {
        validateEmail(email);
        return repository.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with email: " + email));
    }

    private static void validateBasicPersonDataForCurrentUser(Person person, List<String> errors) {
        if (person == null) {
            errors.add("Updated person data cannot be null");
            return;
        }
        validateField(person.getName(), "Person name", errors);
    }

    private static void validateCurrentPasswordProvided(String currentPassword, List<String> errors) {
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            errors.add("Current password must be provided for user update");
        }
    }

    public static void validateCurrentPassword(String providedPassword, String storedPassword) {
        if (providedPassword == null || !providedPassword.equals(storedPassword)) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
    }
}