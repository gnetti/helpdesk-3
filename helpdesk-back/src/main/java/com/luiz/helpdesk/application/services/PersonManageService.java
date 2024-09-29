package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.application.ports.out.DecryptionPort;
import com.luiz.helpdesk.application.ports.out.PasswordEncoderPort;
import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.exception.person.InvalidPasswordException;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.domain.validator.PersonValidator;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonManageService implements PersonManageUseCasePort {

    private final PersonPersistenceOutputPort personRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final DecryptionPort decryptionService;

    public PersonManageService(PersonPersistenceOutputPort personRepository,
                               @Lazy PasswordEncoderPort passwordEncoder,
                               @Lazy DecryptionPort decryptionService) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.decryptionService = decryptionService;
    }

    @Override
    @Transactional
    public Person createPerson(Person person) {
        PersonValidator.validateForCreation(person, personRepository);
        return personRepository.save(encodePassword(person));
    }

    @Override
    @Transactional
    public Person updatePersonWithAddress(Integer id, Person updatedPerson) {
        Person existingPerson = getPersonById(id).orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
        PersonValidator.validateForUpdate(updatedPerson, personRepository, existingPerson);
        Person personToUpdate = existingPerson.updateFieldsAndAddress(updatedPerson);
        return personRepository.update(encodePassword(personToUpdate));
    }

    @Override
    public Pagination<Person> getAllPersons(Pagination<?> pagination) {
        return personRepository.getAllPersons(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> getPersonById(Integer id) {
        PersonValidator.validateId(id);
        return personRepository.findById(id);
    }

    @Override
    @Transactional
    public boolean deletePerson(Integer id) {
        PersonValidator.validateId(id);
        PersonValidator.validatePersonExists(personRepository, id);
        return personRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findPersonByCpf(String cpf) {
        PersonValidator.validateCpf(cpf);
        return personRepository.findByCpf(cpf);
    }

    @Override
    @Transactional(readOnly = true)
    public Person findPersonByEmail(String email) {
        return PersonValidator.validateAndGetPersonByEmail(personRepository, email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsPersonById(Integer id) {
        PersonValidator.validateId(id);
        return personRepository.existsById(id);
    }

    @Override
    @Transactional
    public Person updateCurrentUser(String email, Person updatedPerson, String currentPassword, String newPassword) throws Exception {
        Person existingPerson = findPersonByEmail(email);
        PersonValidator.validateForCurrentUserUpdate(updatedPerson, currentPassword, newPassword, personRepository, existingPerson);
        verifyCurrentPassword(email, currentPassword);
        Person personToUpdate = updatePersonFields(existingPerson, updatedPerson, newPassword);
        return personRepository.update(personToUpdate);
    }

    @Override
    public boolean verifyPassword(String email, String password) {
        return personRepository.findByEmail(email)
                .map(person -> passwordEncoder.matches(password, person.getPassword()))
                .orElse(false);
    }

    @Override
    public Person encodePassword(Person person) {
        return person.withPassword(passwordEncoder.encode(person.getPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findPersonByEmailAndIdNot(String email, Integer id) {
        PersonValidator.validateEmail(email);
        PersonValidator.validateId(id);
        return personRepository.findByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsPersonByCpfAndIdNot(String cpf, Integer id) {
        PersonValidator.validateCpf(cpf);
        PersonValidator.validateId(id);
        return personRepository.existsByCpfAndIdNot(cpf, id);
    }

    @Override
    public boolean existsPersonByEmailAndIdNot(String email, Integer id) {
        PersonValidator.validateEmail(email);
        PersonValidator.validateId(id);
        return personRepository.existsByEmailAndIdNot(email, id);
    }

    private void verifyCurrentPassword(String email, String currentPassword) throws Exception {
        if (!verifyPassword(email, currentPassword)) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
    }

    private Person updatePersonFields(Person existingPerson, Person updatedPerson, String newPassword) {
        Person.Builder builder = existingPerson.toBuilder()
                .withTheme(updatedPerson.getTheme());

        if (newPassword != null && !newPassword.isEmpty()) {
            validateNewPassword(existingPerson, newPassword);
            builder.withPassword(passwordEncoder.encode(newPassword));
        }

        return builder.build();
    }

    private void validateNewPassword(Person existingPerson, String newPassword) {
        if (passwordEncoder.matches(newPassword, existingPerson.getPassword())) {
            throw new InvalidPasswordException("New password must be different from the current password");
        }
    }
}