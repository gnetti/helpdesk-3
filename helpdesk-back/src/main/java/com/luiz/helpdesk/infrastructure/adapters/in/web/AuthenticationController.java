package com.luiz.helpdesk.infrastructure.adapters.in.web;

import com.luiz.helpdesk.application.ports.in.AuthenticationServicePort;
import com.luiz.helpdesk.application.ports.out.DecryptionPort;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.AuthenticationDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationServicePort authenticationService;
    private final DecryptionPort decryptionPort;

    public AuthenticationController(AuthenticationServicePort authenticationService, DecryptionPort decryptionPort) {
        this.authenticationService = authenticationService;
        this.decryptionPort = decryptionPort;
        logger.info("AuthenticationController initialized");
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody AuthenticationDTO request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        try {
            logger.debug("Attempting to decrypt password");
            String decryptedPassword = decryptionPort.decrypt(request.getPassword());
            logger.debug("Decrypted password length: {}", decryptedPassword.length());

            logger.debug("Attempting to authenticate user");
            Person authenticatedPerson = authenticationService.authenticate(request.getEmail(), decryptedPassword);
            logger.info("User authenticated successfully: {}", authenticatedPerson.getEmail());

            logger.debug("Generating token for authenticated user");
            String token = authenticationService.generateToken(authenticatedPerson);
            logger.debug("Token generated successfully");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            logger.info("Login successful for user: {}", authenticatedPerson.getEmail());

            return ResponseEntity.ok().headers(headers).build();
        } catch (Exception e) {
            logger.error("Authentication failed for email: {}. Error: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}