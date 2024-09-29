package com.luiz.helpdesk.application.ports.in;

import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;

import java.util.Optional;

public interface PersonManageUseCasePort {
    Person createPerson(Person person);

    Person updatePersonWithAddress(Integer id, Person updatedPerson);

    Pagination<Person> getAllPersons(Pagination<?> pagination);

    Optional<Person> getPersonById(Integer id);

    boolean deletePerson(Integer id);

    Optional<Person> findPersonByCpf(String cpf);

    Person findPersonByEmail(String email) throws PersonNotFoundException;

    boolean existsPersonById(Integer id);

    boolean existsPersonByCpfAndIdNot(String cpf, Integer id);

    boolean existsPersonByEmailAndIdNot(String email, Integer id);

    Person updateCurrentUser(String email, Person updatedPerson, String encryptedCurrentPassword, String newPassword) throws Exception;

    Optional<Person> findPersonByEmailAndIdNot(String email, Integer id);

    boolean verifyPassword(String email, String password);

    Person encodePassword(Person person);
}