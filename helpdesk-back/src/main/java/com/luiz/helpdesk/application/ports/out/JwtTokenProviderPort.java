package com.luiz.helpdesk.application.ports.out;

import com.luiz.helpdesk.domain.model.Person;

public interface JwtTokenProviderPort {
    String createToken(Person person);

    String getEmailFromToken(String token);

    boolean validateToken(String token);
}
