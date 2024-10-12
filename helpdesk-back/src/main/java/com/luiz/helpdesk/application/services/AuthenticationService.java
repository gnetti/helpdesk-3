package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.AuthenticationUseCasePort;
import com.luiz.helpdesk.application.ports.in.PersonManageUseCasePort;
import com.luiz.helpdesk.application.ports.out.JwtTokenProviderPort;
import com.luiz.helpdesk.application.ports.out.PasswordEncoderPort;
import com.luiz.helpdesk.domain.exception.person.PersonNotFoundException;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.domain.validator.AuthenticationValidator;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements AuthenticationUseCasePort {

    private final PersonManageUseCasePort personUseCase;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtTokenProviderPort jwtTokenProvider;

    public AuthenticationService(PersonManageUseCasePort personUseCase,
                                 PasswordEncoderPort passwordEncoder,
                                 JwtTokenProviderPort jwtTokenProvider) {
        this.personUseCase = personUseCase;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Person authenticate(String email, String password) {
        AuthenticationValidator.validateEmailNotBlank(email);
        AuthenticationValidator.validatePasswordNotBlank(password);
        Person person = findPersonByEmail(email);
        AuthenticationValidator.validateCredentials(password, person.getPassword(), passwordEncoder);
        return person;
    }

    @Override
    public String generateToken(Person person) {
        return jwtTokenProvider.createToken(person);
    }

    @Override
    public CustomUserDetails getAuthenticatedUser() {
        return AuthenticationValidator.validateAndGetUserDetails(SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public String refreshToken(String oldToken) {
        AuthenticationValidator.validateToken(jwtTokenProvider.validateToken(oldToken));
        String email = jwtTokenProvider.getEmailFromToken(oldToken);
        Person person = findPersonByEmail(email);
        return jwtTokenProvider.createToken(person);
    }

    private Person findPersonByEmail(String email) {
        try {
            return personUseCase.findPersonByEmail(email);
        } catch (PersonNotFoundException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    private void validateCredentials(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}