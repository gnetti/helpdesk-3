package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.VerifyLoggedUserUseCase;
import com.luiz.helpdesk.application.ports.out.JwtTokenProviderPort;
import com.luiz.helpdesk.domain.exception.auth.UnauthorizedException;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VerifyLoggedUserService implements VerifyLoggedUserUseCase {

    private final JwtTokenProviderPort jwtTokenProvider;

    public VerifyLoggedUserService(JwtTokenProviderPort jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public CustomUserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UsernameNotFoundException("Usuário não autenticado");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        return (CustomUserDetails) principal;
    }

    @Override
    public String refreshToken(String oldToken) {
        if (!jwtTokenProvider.validateToken(oldToken)) {
            throw new UnauthorizedException("Token inválido ou expirado");
        }
        CustomUserDetails userDetails = getAuthenticatedUser();
        Person person = Person.fromCustomUserDetails(userDetails);
        return jwtTokenProvider.createToken(person);
    }
}