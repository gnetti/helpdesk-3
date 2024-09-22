package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.AuthenticationServicePort;
import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.application.ports.out.JwtTokenProviderPort;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Person;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements AuthenticationServicePort {

    private final PersonManageUseCasePort personUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProviderPort jwtTokenProvider;

    public AuthenticationService(PersonManageUseCasePort personUseCase,
                                 PasswordEncoder passwordEncoder,
                                 JwtTokenProviderPort jwtTokenProvider) {
        this.personUseCase = personUseCase;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Person authenticate(String email, String password) {
        try {
            Person person = personUseCase.findPersonByEmail(email);
            if (passwordEncoder.matches(password, person.getPassword())) {
                return person;
            } else {
                throw new BadCredentialsException("Invalid email or password");
            }
        } catch (PersonNotFoundException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Override
    public String generateToken(Person person) {
        return jwtTokenProvider.createToken(person);
    }
}