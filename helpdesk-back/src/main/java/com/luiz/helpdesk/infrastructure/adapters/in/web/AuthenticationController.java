package com.luiz.helpdesk.infrastructure.adapters.in.web;

import com.luiz.helpdesk.application.ports.in.AuthenticationServicePort;
import com.luiz.helpdesk.application.ports.out.DecryptionPort;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.AuthenticationDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationServicePort authenticationService;
    private final DecryptionPort decryptionPort;

    public AuthenticationController(AuthenticationServicePort authenticationService, DecryptionPort decryptionPort) {
        this.authenticationService = authenticationService;
        this.decryptionPort = decryptionPort;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationDTO> login(@Valid @RequestBody AuthenticationDTO request) {
        try {
            String decryptedPassword = decryptionPort.decrypt(request.getPassword());
            Person authenticatedPerson = authenticationService.authenticate(request.getEmail(), decryptedPassword);
            String token = authenticationService.generateToken(authenticatedPerson);
            AuthenticationDTO response = AuthenticationDTO.fromDomainModel(token, authenticatedPerson);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}