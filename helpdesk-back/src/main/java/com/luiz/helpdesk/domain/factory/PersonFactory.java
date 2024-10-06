package com.luiz.helpdesk.domain.factory;

import com.luiz.helpdesk.domain.model.Person;

import java.time.LocalDate;

public interface PersonFactory {
    Person createPerson(String name,
                        String cpf,
                        String email,
                        String password,
                        Integer profile,
                        LocalDate creationDate,
                        Integer theme);
}