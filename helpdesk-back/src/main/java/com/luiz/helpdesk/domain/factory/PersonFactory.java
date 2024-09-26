package com.luiz.helpdesk.domain.factory;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.Person;

import java.time.LocalDate;
import java.util.Set;

public interface PersonFactory {
    Person createPerson(String name,
                        String cpf,
                        String email,
                        String password,
                        Set<Profile> profiles,
                        LocalDate creationDate,
                        String theme);
}