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

}