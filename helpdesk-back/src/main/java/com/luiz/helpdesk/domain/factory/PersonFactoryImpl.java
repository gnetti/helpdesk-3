package com.luiz.helpdesk.domain.factory;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.enums.Theme;
import com.luiz.helpdesk.domain.model.Person;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class PersonFactoryImpl implements PersonFactory {

    @Override
    public Person createPerson(
            String name,
            String cpf,
            String email,
            String password,
            Set<Profile> profiles,
            LocalDate creationDate,
            String theme) {
        return Person.builder()
                .withName(name)
                .withCpf(cpf)
                .withEmail(email)
                .withPassword(password)
                .withProfiles(profiles)
                .withCreationDate(creationDate)
                .withTheme(theme != null ? Theme.valueOf(theme) : Theme.INDIGO_PINK)
                .build();
    }

    @Override
    public String toString() {
        return "PersonFactoryImpl{}";
    }
}