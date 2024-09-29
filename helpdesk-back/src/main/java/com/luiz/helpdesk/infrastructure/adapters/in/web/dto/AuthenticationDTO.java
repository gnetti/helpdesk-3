package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationDTO {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationDTO.class);

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public AuthenticationDTO() {
        logger.debug("Creating new AuthenticationDTO");
    }

    public AuthenticationDTO(String email, String password) {
        this.email = email;
        this.password = password;
        logger.debug("Creating new AuthenticationDTO with email: {}", email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        logger.debug("Setting email: {}", email);
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        logger.debug("Setting password: [PROTECTED]");
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthenticationDTO{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}