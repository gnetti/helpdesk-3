package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.AuthenticationUseCasePort;
import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.application.ports.out.DecryptionPort;
import com.luiz.helpdesk.application.ports.out.PasswordEncoderPort;
import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.exception.auth.UnauthorizedException;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.domain.validator.PersonValidator;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonManageService implements PersonManageUseCasePort {

    private final PersonPersistenceOutputPort personRepository;
    private final AuthenticationUseCasePort authenticationUseCasePort;
    private final PasswordEncoderPort passwordEncoder;
    private final DecryptionPort decryptionService;

    public PersonManageService(PersonPersistenceOutputPort personRepository,
                               @Lazy AuthenticationUseCasePort authenticationUseCasePort,
                               @Lazy PasswordEncoderPort passwordEncoder,
                               DecryptionPort decryptionService) {
        this.personRepository = personRepository;
        this.authenticationUseCasePort = authenticationUseCasePort;
        this.passwordEncoder = passwordEncoder;
        this.decryptionService = decryptionService;
    }

    @Override
    @Transactional
    public Person createPerson(Person person) throws Exception {
        PersonValidator.validateCpfAndPassword(person.getCpf(), person.getPassword());
        PersonValidator.validateForCreation(person, personRepository);
        return personRepository.save(encodePassword(person));
    }

    @Override
    @Transactional
    public Person updatePersonWithAddress(Integer id, Person updatedPerson) throws Exception {
        Person existingPerson = getExistingPerson(id);
        PersonValidator.validateForUpdate(updatedPerson, personRepository, existingPerson);
        Person personToUpdate = existingPerson.updateFieldsAndAddress(updatedPerson);
        return personRepository.update(encodePassword(personToUpdate));
    }

    @Override
    @Transactional(readOnly = true)
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
    public Person updateCurrentUser(Integer id, Person updatedPerson, String currentPassword, String newPassword) throws Exception {
        CustomUserDetails userDetails = authenticationUseCasePort.getAuthenticatedUser();
        validateCurrentUserOperation(userDetails.getId(), id);
        Person existingPerson = getExistingPerson(id);
        String decryptedCurrentPassword = decryptPassword(currentPassword);
        String decryptedNewPassword = decryptPassword(newPassword);
        validateCurrentPassword(decryptedCurrentPassword, existingPerson.getPassword());
        PersonValidator.validateForCurrentUserUpdate(updatedPerson, decryptedCurrentPassword, decryptedNewPassword, existingPerson);
        Person personToUpdate = updatePersonFields(existingPerson, updatedPerson, decryptedNewPassword);
        String encodedNewPassword = encodeNewPassword(decryptedNewPassword);

        return personRepository.updateCurrentUser(userDetails.getId(), personToUpdate, decryptedCurrentPassword, encodedNewPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public Person getCurrentUser() {
        CustomUserDetails userDetails = authenticationUseCasePort.getAuthenticatedUser();
        try {
            return personRepository.getCurrentUser(userDetails.getId());
        } catch (PersonNotFoundException e) {
            throw new UnauthorizedException("Current user not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyPassword(String email, String password) throws Exception {
        String decryptedPassword = decryptionService.decrypt(password);
        return personRepository.findByEmail(email)
                .map(person -> passwordEncoder.matches(decryptedPassword, person.getPassword()))
                .orElse(false);
    }

    @Override
    @Transactional
    public Person encodePassword(Person person) throws Exception {
        String decryptedPassword = decryptionService.decrypt(person.getPassword());
        return person.withPassword(passwordEncoder.encode(decryptedPassword));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findPersonByEmailAndIdNot(String email, Integer id) {
        PersonValidator.validateEmail(email);
        PersonValidator.validateId(id);
        return personRepository.findByEmailAndIdNot(email, id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsPersonByCpfAndIdNot(String cpf, Integer id) {
        PersonValidator.validateCpf(cpf);
        PersonValidator.validateId(id);
        return personRepository.existsByCpfAndIdNot(cpf, id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsPersonByEmailAndIdNot(String email, Integer id) {
        PersonValidator.validateEmail(email);
        PersonValidator.validateId(id);
        return personRepository.existsByEmailAndIdNot(email, id);
    }

    private void validateCurrentUserOperation(Integer tokenUserId, Integer requestUserId) {
        if (!tokenUserId.equals(requestUserId)) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }
    }

    private Person getExistingPerson(Integer id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
    }

    private Person updatePersonFields(Person existingPerson, Person updatedPerson, String newPassword) {
        Person.Builder builder = existingPerson.toBuilder();
        if (updatedPerson.getTheme() != null) {
            builder.withTheme(updatedPerson.getTheme());
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            builder.withPassword(newPassword);
        }
        return builder.build();
    }

    private String decryptPassword(String password) throws Exception {
        return password != null ? decryptionService.decrypt(password) : null;
    }

    private void validateCurrentPassword(String decryptedCurrentPassword, String storedPassword) {
        if (!passwordEncoder.matches(decryptedCurrentPassword, storedPassword)) {
            throw new UnauthorizedException("Current password is incorrect");
        }
    }

    private String encodeNewPassword(String decryptedNewPassword) {
        return decryptedNewPassword != null && !decryptedNewPassword.isEmpty()
                ? passwordEncoder.encode(decryptedNewPassword)
                : null;
    }
}