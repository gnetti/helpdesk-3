package com.luiz.helpdesk.application.ports.in;

import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;

public interface AuthenticationUseCasePort {
    Person authenticate(String email, String password);
    String generateToken(Person person);
    CustomUserDetails getAuthenticatedUser();
    String refreshToken(String oldToken);
}
