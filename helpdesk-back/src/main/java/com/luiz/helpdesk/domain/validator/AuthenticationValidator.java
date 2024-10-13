package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.application.ports.out.PasswordEncoderPort;
import com.luiz.helpdesk.domain.exception.auth.UnauthorizedException;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

public class AuthenticationValidator {
    public static void validateCredentials(String rawPassword, String encodedPassword, PasswordEncoderPort passwordEncoder) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }
    }

    public static CustomUserDetails validateAndGetUserDetails(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public static void validateToken(boolean isValid) {
        if (!isValid) {
            throw new UnauthorizedException("Token inválido ou expirado");
        }
    }

    public static void validateEmailNotBlank(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadCredentialsException("O e-mail não pode ficar em branco");
        }
    }

    public static void validatePasswordNotBlank(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BadCredentialsException("A senha não pode ficar em branco");
        }
    }

}