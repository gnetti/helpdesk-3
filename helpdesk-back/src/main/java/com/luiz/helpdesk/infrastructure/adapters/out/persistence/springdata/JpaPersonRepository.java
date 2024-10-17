package com.luiz.helpdesk.infrastructure.adapters.out.persistence.springdata;

import com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity.PersonEntity;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.PersonFilterOperationsUtil;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.SpecificationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@Repository
public interface JpaPersonRepository extends JpaRepository<PersonEntity, Integer>, JpaSpecificationExecutor<PersonEntity> {

    Optional<PersonEntity> findByCpf(String cpf);

    Optional<PersonEntity> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpfAndIdNot(String cpf, Integer id);

    boolean existsByEmailAndIdNot(String email, Integer id);

    Page<PersonEntity> findAll(Pageable pageable);

    Optional<PersonEntity> findByEmailAndIdNot(String email, Integer id);

    @Query("SELECT p FROM PersonEntity p LEFT JOIN FETCH p.profiles LEFT JOIN FETCH p.themes LEFT JOIN FETCH p.address WHERE p.id = :id")
    Optional<PersonEntity> findByIdWithAssociations(Integer id);

    long count();

    default Map<String, BiFunction<String, Object, SpecificationUtil.FilterOperation>> getPersonFilterOperations() {
        return PersonFilterOperationsUtil.getFilterOperations();
    }

    Page<PersonEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<PersonEntity> findByCpfContaining(String cpf, Pageable pageable);

    Page<PersonEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    @Query("SELECT p FROM PersonEntity p JOIN p.profiles pr WHERE pr IN :profiles")
    Page<PersonEntity> findByProfilesIn(List<String> profiles, Pageable pageable);

    Page<PersonEntity> findByCreationDate(LocalDate creationDate, Pageable pageable);

    Page<PersonEntity> findByCreationDateGreaterThanEqual(LocalDate creationDateFrom, Pageable pageable);

    Page<PersonEntity> findByCreationDateLessThanEqual(LocalDate creationDateTo, Pageable pageable);

    @Query("SELECT p FROM PersonEntity p JOIN p.themes t WHERE t IN :themes")
    Page<PersonEntity> findByThemesIn(List<String> themes, Pageable pageable);

}