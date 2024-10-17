package com.luiz.helpdesk.application.ports.in;

import com.luiz.helpdesk.domain.exception.auth.UnauthorizedException;
import com.luiz.helpdesk.domain.exception.person.InvalidPasswordException;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;

import java.util.Map;
import java.util.Optional;

public interface PersonManageUseCasePort {

    Person createPerson(Person person) throws Exception;

    Person updatePersonWithAddress(Integer id, Person updatedPerson) throws Exception;

    Pagination<Person> getAllPersons(Pagination<?> pagination);

    Pagination<Person> getAllPersonsWithFilters(Pagination<?> pagination, Map<String, String> filters);

    Pagination<Person> getAllPersonsWithFilters(Pagination<?> pagination, String sortBy, String sortDirection, Map<String, String> filters);

    Optional<Person> getPersonById(Integer id);

    boolean deletePerson(Integer id);

    Optional<Person> findPersonByCpf(String cpf);

    Person findPersonByEmail(String email) throws PersonNotFoundException;

    boolean existsPersonById(Integer id);

    boolean existsPersonByCpfAndIdNot(String cpf, Integer id);

    boolean existsPersonByEmailAndIdNot(String email, Integer id);

    Person updateCurrentUser(Integer id, Person updatedPerson, String currentPassword, String newPassword) throws Exception;

    Person getCurrentUser() throws UnauthorizedException;

    Optional<Person> findPersonByEmailAndIdNot(String email, Integer id);

    boolean verifyPassword(String email, String password) throws Exception;

    Person encodePassword(Person person) throws Exception;
}