package com.luiz.helpdesk.infrastructure.adapters.out.persistence.springdata;

import com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity.TokenTimeProfilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaTokenTimeRepository extends JpaRepository<TokenTimeProfilesEntity, Integer> {

    Optional<TokenTimeProfilesEntity> findByProfile(Integer profile);

    boolean existsByProfile(Integer profile);
}