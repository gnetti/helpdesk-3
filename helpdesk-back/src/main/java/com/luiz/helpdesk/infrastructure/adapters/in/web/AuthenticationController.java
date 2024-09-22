package com.luiz.helpdesk.infrastructure.adapters.in.web;

import com.luiz.helpdesk.application.ports.in.AuthenticationServicePort;
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

    public AuthenticationController(AuthenticationServicePort authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationDTO> login(@Valid @RequestBody AuthenticationDTO request) {
        Person authenticatedPerson = authenticationService.authenticate(request.getEmail(), request.getPassword());
        String token = authenticationService.generateToken(authenticatedPerson);

        AuthenticationDTO response = AuthenticationDTO.fromDomainModel(token, authenticatedPerson);

        return ResponseEntity.ok(response);
    }
}