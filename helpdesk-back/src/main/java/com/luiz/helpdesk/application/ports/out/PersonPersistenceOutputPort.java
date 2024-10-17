package com.luiz.helpdesk.application.ports.out;

import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;

import java.util.Map;
import java.util.Optional;

public interface PersonPersistenceOutputPort {

    Person save(Person person);

    Person update(Person person);

    Optional<Person> findById(Integer id);

    boolean deleteById(Integer id);

    Optional<Person> findByCpf(String cpf);

    Optional<Person> findByEmail(String email);

    boolean existsById(Integer id);

    boolean existsByCpfAndIdNot(String cpf, Integer id);

    boolean existsByEmailAndIdNot(String email, Integer id);

    Pagination<Person> getAllPersons(Pagination<?> pagination);

    Pagination<Person> getAllPersonsWithFilters(Pagination<?> pagination, String sortBy, String sortDirection, Map<String, String> filters);

    Optional<Person> findByEmailAndIdNot(String email, Integer id);

    Person getCurrentUser(Integer id);

    Person updateCurrentUser(Integer id, Person updatedPerson, String encryptedCurrentPassword, String newPassword) throws Exception;
}