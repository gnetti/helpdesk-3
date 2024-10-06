package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.domain.exception.auth.UnauthorizedException;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationValidator {

    public static void validateCredentials(String rawPassword, String encodedPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    public static CustomUserDetails validateAndGetUserDetails(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UnauthorizedException("User not authenticated");
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public static void validateToken(boolean isValid) {
        if (!isValid) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    public static void validateEmailNotBlank(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadCredentialsException("Email cannot be blank");
        }
    }

    public static void validatePasswordNotBlank(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BadCredentialsException("Password cannot be blank");
        }
    }
}