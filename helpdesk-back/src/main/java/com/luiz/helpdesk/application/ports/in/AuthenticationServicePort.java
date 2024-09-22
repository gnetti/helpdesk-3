package com.luiz.helpdesk.application.ports.in;

import com.luiz.helpdesk.domain.model.Person;

public interface AuthenticationServicePort {
    Person authenticate(String email, String password);

    String generateToken(Person person);
}
