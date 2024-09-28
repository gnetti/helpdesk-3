package com.luiz.helpdesk.infrastructure.adapters.out.persistence;

import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity.PersonEntity;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.springdata.JpaPersonRepository;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PersonPersistenceOutputAdapter implements PersonPersistenceOutputPort {

    private final JpaPersonRepository jpaPersonRepository;

    public PersonPersistenceOutputAdapter(JpaPersonRepository jpaPersonRepository) {
        this.jpaPersonRepository = jpaPersonRepository;
    }

    @Override
    @Transactional
    public Person save(Person person) {
        PersonEntity personEntity = PersonEntity.fromDomainModel(person);
        PersonEntity savedEntity = jpaPersonRepository.save(personEntity);
        return savedEntity.toDomainModel();
    }

    @Override
    @Transactional
    public Person update(Person person) {
        if (person.getId() == null) {
            throw new IllegalArgumentException("Person ID cannot be null for update operation");
        }

        return jpaPersonRepository.findById(person.getId())
                .map(personEntity -> {
                    personEntity.updateFromDomainModel(person);
                    PersonEntity updatedEntity = jpaPersonRepository.save(personEntity);
                    return updatedEntity.toDomainModel();
                })
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + person.getId()));
    }

    @Override
    public Optional<Person> findById(Integer id) {
        return jpaPersonRepository.findById(id).map(PersonEntity::toDomainModel);
    }

    @Override
    @Transactional
    public boolean deleteById(Integer id) {
        if (jpaPersonRepository.existsById(id)) {
            jpaPersonRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Person> findByCpf(String cpf) {
        return jpaPersonRepository.findByCpf(cpf).map(PersonEntity::toDomainModel);
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        return jpaPersonRepository.findByEmail(email).map(PersonEntity::toDomainModel);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpaPersonRepository.existsById(id);
    }

    @Override
    public boolean existsByCpfAndIdNot(String cpf, Integer id) {
        return jpaPersonRepository.existsByCpfAndIdNot(cpf, id);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Integer id) {
        return jpaPersonRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public Pagination<Person> getAllPersons(Pagination<?> pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.pageNumber(), pagination.pageSize());
        Page<PersonEntity> page = jpaPersonRepository.getAllPersons(pageRequest);
        return PaginationUtil.mapPageToPagination(page, PersonEntity::toDomainModel);
    }
}