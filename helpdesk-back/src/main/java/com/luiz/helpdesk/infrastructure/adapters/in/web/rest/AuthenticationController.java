package com.luiz.helpdesk.infrastructure.adapters.in.web.rest;

import com.luiz.helpdesk.application.ports.in.AuthenticationUseCasePort;
import com.luiz.helpdesk.application.ports.out.DecryptionPort;
import com.luiz.helpdesk.domain.exception.auth.UnauthorizedException;
import com.luiz.helpdesk.domain.model.Person;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.AuthenticationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthenticationController {

    private final AuthenticationUseCasePort authenticationService;
    private final DecryptionPort decryptionPort;

    public AuthenticationController(AuthenticationUseCasePort authenticationService, DecryptionPort decryptionPort) {
        this.authenticationService = authenticationService;
        this.decryptionPort = decryptionPort;
    }

    @PostMapping("/login")
    @Operation(summary = "Create the authentication token", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Void> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User credentials", required = true,
                    content = @Content(schema = @Schema(implementation = AuthenticationDTO.class)))
            @Valid @RequestBody AuthenticationDTO request) throws Exception {
        String decryptedPassword = decryptionPort.decrypt(request.getPassword());
        Person authenticatedPerson = authenticationService.authenticate(request.getEmail(), decryptedPassword);
        String token = authenticationService.generateToken(authenticatedPerson);
        return createTokenResponse(token);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh the authentication token", description = "Refreshes an existing JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<String> refreshToken(
            @Parameter(description = "JWT token to be refreshed", required = true)
            @RequestHeader("Authorization") String token) {
        String newToken = authenticationService.refreshToken(extractToken(token));
        return ResponseEntity.ok(newToken);
    }

    private ResponseEntity<Void> createTokenResponse(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return ResponseEntity.ok().headers(headers).build();
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new UnauthorizedException("Invalid authorization header");
    }
}