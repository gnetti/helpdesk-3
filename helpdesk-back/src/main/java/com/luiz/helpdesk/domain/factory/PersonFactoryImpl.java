package com.luiz.helpdesk.domain.factory;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.enums.Theme;
import com.luiz.helpdesk.domain.model.Person;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PersonFactoryImpl implements PersonFactory {

    @Override
    public Person createPerson(
            String name,
            String cpf,
            String email,
            String password,
            Integer profile,
            LocalDate creationDate,
            Integer theme) {
        return Person.builder()
                .withName(name)
                .withCpf(cpf)
                .withEmail(email)
                .withPassword(password)
                .withProfile(profile != null ? profile : Profile.CLIENT.getCode())
                .withCreationDate(creationDate)
                .withTheme(theme != null ? theme : Theme.INDIGO_PINK.getCode())
                .build();
    }

    @Override
    public String toString() {
        return "PersonFactoryImpl{}";
    }
}