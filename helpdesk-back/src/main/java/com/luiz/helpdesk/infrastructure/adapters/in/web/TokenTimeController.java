package com.luiz.helpdesk.infrastructure.adapters.in.web;

import com.luiz.helpdesk.application.ports.in.TokenTimeManagementUseCasePort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.TokenTimeProfileDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/token-time")
public class TokenTimeController {

    private final TokenTimeManagementUseCasePort tokenTimeService;

    public TokenTimeController(TokenTimeManagementUseCasePort tokenTimeService) {
        this.tokenTimeService = tokenTimeService;
    }

    @PostMapping
    public ResponseEntity<TokenTimeProfileDTO> create(@Valid @RequestBody TokenTimeProfileDTO tokenTimeProfileDTO) {
        TokenTimeProfileDTO createdProfile = tokenTimeService.create(tokenTimeProfileDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{profile}")
                .buildAndExpand(createdProfile.getProfile()).toUri();
        return ResponseEntity.created(uri).body(createdProfile);
    }

    @PutMapping("/{profile}")
    public ResponseEntity<TokenTimeProfileDTO> update(@PathVariable Profile profile, @Valid @RequestBody TokenTimeProfileDTO tokenTimeProfileDTO) {
        TokenTimeProfileDTO updatedProfile = tokenTimeService.update(profile, tokenTimeProfileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/{profile}")
    public ResponseEntity<TokenTimeProfileDTO> findByProfile(@PathVariable Profile profile) {
        return tokenTimeService.findByProfile(profile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TokenTimeProfileDTO>> findAll() {
        List<TokenTimeProfileDTO> profiles = tokenTimeService.findAll();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/expiration")
    public ResponseEntity<Long> getExpirationTimeInMillis(@RequestParam Profile profile) {
        long expirationTime = tokenTimeService.getExpirationTimeInMillis(profile);
        return ResponseEntity.ok(expirationTime);
    }

    @GetMapping("/profile")
    public ResponseEntity<TokenTimeProfileDTO> getTokenTimeProfile(@RequestParam Profile profile) {
        TokenTimeProfileDTO tokenTimeProfileDTO = tokenTimeService.getTokenTimeProfile(profile);
        return ResponseEntity.ok(tokenTimeProfileDTO);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByProfile(@RequestParam Profile profile) {
        boolean exists = tokenTimeService.existsByProfile(profile);
        return ResponseEntity.ok(exists);
    }
}