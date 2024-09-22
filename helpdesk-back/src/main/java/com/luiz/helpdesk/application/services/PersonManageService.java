package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.domain.validator.PersonValidator;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Service
public class PersonManageService implements PersonManageUseCasePort {

    private final PersonPersistenceOutputPort personRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PersonManageService(PersonPersistenceOutputPort personRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Person createPerson(Person person) {
        PersonValidator.validatePersonForCreation(person);
        PersonValidator.validatePersonDoesNotExist(personRepository, person);
        return personRepository.save(person.withEncodedPassword(passwordEncoder));
    }

    @Override
    @Transactional
    public Person updatePersonWithAddress(Integer id, Person updatedPerson) {
        PersonValidator.validatePersonForUpdate(updatedPerson);
        return updatePerson(id, existingPerson -> {
            Person personToUpdate = existingPerson.updateFieldsAndAddress(updatedPerson, passwordEncoder);
            PersonValidator.validateUniqueFields(personRepository, personToUpdate, existingPerson);
            return personToUpdate;
        });
    }

    private Person updatePerson(Integer id, Function<Person, Person> updateFunction) {
        PersonValidator.validateId(id);
        return personRepository.findById(id)
                .map(updateFunction)
                .map(personRepository::update)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
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
        PersonValidator.validateEmail(email);
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsPersonById(Integer id) {
        PersonValidator.validateId(id);
        return personRepository.existsById(id);
    }
}