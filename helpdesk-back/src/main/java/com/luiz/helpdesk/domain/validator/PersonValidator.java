package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.exception.person.InvalidPersonDataException;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Address;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.domain.enums.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PersonValidator {

    public static void validateForCreation(Person person, PersonPersistenceOutputPort repository) {
        List<String> errors = new ArrayList<>();
        validateBasicPersonData(person, errors);
        validatePasswordNotEmpty(person.getPassword(), errors);
        validateProfile(person.getProfile(), errors);
        validateUniqueFields(repository, person, null, errors);
        throwIfErrors(errors);
    }

    public static void validateForUpdate(Person person, PersonPersistenceOutputPort repository, Person existingPerson) {
        List<String> errors = new ArrayList<>();
        validateBasicPersonData(person, errors);
        validateProfile(person.getProfile(), errors);
        validateUniqueFields(repository, person, existingPerson, errors);
        throwIfErrors(errors);
    }

    public static void validateForCurrentUserUpdate(Person updatedPerson, String currentPassword, String newPassword, Person existingPerson) {
        List<String> errors = new ArrayList<>();
        validateTheme(updatedPerson.getTheme(), errors);
        validatePasswordUpdateFields(currentPassword, newPassword, errors);
        validateAtLeastOneFieldUpdated(updatedPerson.getTheme(), newPassword, existingPerson.getTheme(), errors);
        throwIfErrors(errors);
    }

    private static void validateBasicPersonData(Person person, List<String> errors) {
        if (person == null) {
            errors.add("A pessoa não pode ser nula");
            return;
        }
        validateField(person.getName(), "Nome da pessoa", errors);
        validateField(person.getCpf(), "CPF da pessoa", errors);
        validateField(person.getEmail(), "E-mail da pessoa", errors);
        validateAddress(person.getAddress(), errors);
    }

    private static void validateAddress(Address address, List<String> errors) {
        if (address == null) {
            errors.add("A pessoa deve ter um endereço");
            return;
        }
        validateField(address.getStreet(), "Rua do endereço", errors);
        validateField(address.getNeighborhood(), "Bairro do endereço", errors);
        validateField(address.getCity(), "Cidade do endereço", errors);
        validateField(address.getState(), "Estado do endereço", errors);
        validateField(address.getZipCode(), "CEP do endereço", errors);
        validateField(address.getNumber(), "Número do endereço", errors);
    }

    private static void validateField(String field, String fieldName, List<String> errors) {
        if (field == null || field.trim().isEmpty()) {
            errors.add(fieldName + " não pode ser nulo ou vazio");
        }
    }

    private static void validatePasswordNotEmpty(String password, List<String> errors) {
        validateField(password, "Senha", errors);
    }

    private static void validateProfile(Integer profile, List<String> errors) {
        if (profile == null) {
            errors.add("O perfil não pode ser nulo");
        } else {
            try {
                Profile.fromCode(profile);
            } catch (IllegalArgumentException e) {
                errors.add("Código de perfil inválido");
            }
        }
    }

    private static void validateTheme(Integer theme, List<String> errors) {
        if (theme != null && theme < 0) {
            errors.add("O tema deve ser um número inteiro não negativo");
        }
    }

    private static void validatePasswordUpdateFields(String currentPassword, String newPassword, List<String> errors) {
        if (newPassword != null && !newPassword.isEmpty()) {
            validateCurrentPasswordProvided(currentPassword, errors);
            validateNewPasswordIfProvided(newPassword, errors);
        }
    }

    private static void validateAtLeastOneFieldUpdated(Integer newTheme, String newPassword, Integer existingTheme, List<String> errors) {
        boolean themeChanged = newTheme != null && !newTheme.equals(existingTheme);
        boolean passwordChanged = newPassword != null && !newPassword.isEmpty();
        if (!themeChanged && !passwordChanged) {
            errors.add("Pelo menos um campo (tema ou senha) deve ser atualizado");
        }
    }

    private static void validateUniqueFields(PersonPersistenceOutputPort repository, Person newPerson, Person existingPerson, List<String> errors) {
        if (existingPerson == null || !newPerson.getCpf().equals(existingPerson.getCpf())) {
            validateUniqueField(newPerson.getCpf(), "CPF", () -> repository.existsByCpfAndIdNot(newPerson.getCpf(), newPerson.getId()), errors);
        }
        if (existingPerson == null || !newPerson.getEmail().equals(existingPerson.getEmail())) {
            validateUniqueField(newPerson.getEmail(), "e-mail", () -> repository.existsByEmailAndIdNot(newPerson.getEmail(), newPerson.getId()), errors);
        }
    }

    private static void validateUniqueField(String field, String fieldName, Supplier<Boolean> existsCheck, List<String> errors) {
        if (existsCheck.get()) {
            errors.add("Já existe uma pessoa com " + fieldName + " " + field);
        }
    }

    private static void throwIfErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidPersonDataException(String.join("; ", errors));
        }
    }

    public static void validateId(Integer id) {
        if (id == null) {
            throw new InvalidPersonDataException("O ID não pode ser nulo");
        }
    }

    public static void validateCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new InvalidPersonDataException("O CPF não pode ser nulo ou vazio");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidPersonDataException("O e-mail não pode ser nulo ou vazio");
        }
    }

    public static void validatePersonExists(PersonPersistenceOutputPort repository, Integer id) {
        if (!repository.existsById(id)) {
            throw new PersonNotFoundException("Pessoa não encontrada com o id: " + id);
        }
    }

    public static Person validateAndGetPersonByEmail(PersonPersistenceOutputPort repository, String email) {
        validateEmail(email);
        return repository.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException("Pessoa não encontrada com o e-mail: " + email));
    }

    private static void validateCurrentPasswordProvided(String currentPassword, List<String> errors) {
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            errors.add("A senha atual deve ser fornecida ao atualizar a senha");
        }
    }

    private static void validateNewPasswordIfProvided(String newPassword, List<String> errors) {
        if (newPassword != null) {
            if (newPassword.trim().isEmpty()) {
                errors.add("A nova senha não pode estar vazia se fornecida");
            } else if (newPassword.length() < 8) {
                errors.add("A nova senha deve ter pelo menos 8 caracteres");
            }
        }
    }

    public static void validateCpfAndPassword(String cpf, String password) {
        if (cpf == null || cpf.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new InvalidPersonDataException("CPF e Senha são inválidos ou vazios");
        }
    }
}