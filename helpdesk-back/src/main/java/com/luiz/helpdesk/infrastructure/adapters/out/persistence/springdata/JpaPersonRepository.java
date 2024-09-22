package com.luiz.helpdesk.infrastructure.adapters.out.persistence.springdata;

import com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity.PersonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPersonRepository extends JpaRepository<PersonEntity, Integer> {
    Optional<PersonEntity> findByCpf(String cpf);

    Optional<PersonEntity> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpfAndIdNot(String cpf, Integer id);

    boolean existsByEmailAndIdNot(String email, Integer id);

    @Query("SELECT p FROM PersonEntity p")
    Page<PersonEntity> getAllPersons(Pageable pageable);


    long count();
}