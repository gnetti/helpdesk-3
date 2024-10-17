package com.luiz.helpdesk.infrastructure.adapters.out.persistence;

import com.luiz.helpdesk.application.ports.out.PersonPersistenceOutputPort;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Pagination;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.domain.validator.PaginationValidator;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity.PersonEntity;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.springdata.JpaPersonRepository;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.PaginationUtil;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.PersonFilterOperationsUtil;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.SpecificationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiFunction;

import static org.springframework.data.jpa.repository.query.KeysetScrollSpecification.createSort;

@Component
public class PersonPersistenceOutputAdapter implements PersonPersistenceOutputPort {

    private final JpaPersonRepository jpaPersonRepository;
    private final PaginationValidator paginationValidator;

    public PersonPersistenceOutputAdapter(JpaPersonRepository jpaPersonRepository, PaginationValidator paginationValidator) {
        this.jpaPersonRepository = jpaPersonRepository;
        this.paginationValidator = paginationValidator;
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
        PersonEntity existingEntity = jpaPersonRepository.findById(person.getId())
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + person.getId()));
        existingEntity.updateFromDomainModel(person);
        PersonEntity updatedEntity = jpaPersonRepository.save(existingEntity);
        return updatedEntity.toDomainModel();
    }

    @Override
    public Optional<Person> findById(Integer id) {
        return jpaPersonRepository.findById(id).map(PersonEntity::toDomainModel);
    }

    @Override
    @Transactional
    public boolean deleteById(Integer id) {
        return jpaPersonRepository.findById(id)
                .map(entity -> {
                    jpaPersonRepository.delete(entity);
                    return true;
                })
                .orElse(false);
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
        return getAllPersonsWithFilters(pagination, PaginationValidator.getDefaultSort(), "ASC", new HashMap<>());
    }

    @Override
    public Pagination<Person> getAllPersonsWithFilters(Pagination<?> pagination, String sortBy, String sortDirection, Map<String, String> filters) {
        Sort sort = createSort(sortBy, sortDirection);
        PageRequest pageRequest = PageRequest.of(pagination.pageNumber(), pagination.pageSize(), sort);
        Specification<PersonEntity> spec = createSpecification(filters);
        Page<PersonEntity> page = jpaPersonRepository.findAll(spec, pageRequest);
        return PaginationUtil.mapPageToPagination(page, PersonEntity::toDomainModel);
    }

    @Override
    public Optional<Person> findByEmailAndIdNot(String email, Integer id) {
        return jpaPersonRepository.findByEmailAndIdNot(email, id).map(PersonEntity::toDomainModel);
    }

    @Override
    public Person getCurrentUser(Integer id) {
        return jpaPersonRepository.findById(id)
                .map(PersonEntity::toDomainModel)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
    }

    @Transactional
    @Override
    public Person updateCurrentUser(Integer id, Person updatedPerson, String encryptedCurrentPassword, String newPassword) {
        PersonEntity existingEntity = jpaPersonRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
        existingEntity.updateCurrentUser(updatedPerson.getTheme(), newPassword);
        PersonEntity updatedEntity = jpaPersonRepository.save(existingEntity);
        return updatedEntity.toDomainModel();
    }

    private List<Sort.Order> createSortOrders(String sortBy, String sortDirection) {
        List<Sort.Order> orders = new ArrayList<>();
        String[] sortFields = sortBy.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        for (String field : sortFields) {
            field = paginationValidator.validateSort(field.trim());
            orders.add(new Sort.Order(direction, field));
        }

        return orders;
    }

    private Specification<PersonEntity> createSpecification(Map<String, String> filters) {
        Map<String, BiFunction<String, Object, SpecificationUtil.FilterOperation>> filterOperations = PersonFilterOperationsUtil.getFilterOperations();
        Specification<PersonEntity> spec = Specification.where(null);

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty() && filterOperations.containsKey(key)) {
                SpecificationUtil.FilterOperation operation = filterOperations.get(key).apply(key, value);
                spec = spec.and((root, query, criteriaBuilder) -> operation.buildPredicate(root, criteriaBuilder, value));
            }
        }

        return spec;
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        return Sort.by(direction, sortBy.split(","));
    }
}