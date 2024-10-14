package com.luiz.helpdesk.infrastructure.adapters.in.web.rest;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.luiz.helpdesk.application.ports.in.TokenTimeManagementUseCasePort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.infrastructure.adapters.out.config.serialization.LoginDTOFilter;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.TokenTimeProfileDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
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

    @GetMapping("/all")
    public ResponseEntity<List<TokenTimeProfileDTO>> findAll() {
        return ResponseEntity.ok(tokenTimeService.findAll());
    }

    @PostMapping
    public ResponseEntity<TokenTimeProfileDTO> create(@Valid @RequestBody TokenTimeProfileDTO tokenTimeProfileDTO) {
        TokenTimeProfileDTO createdProfile = tokenTimeService.create(tokenTimeProfileDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{profile}")
                .buildAndExpand(createdProfile.getProfileCode()).toUri();
        return ResponseEntity.created(uri).body(createdProfile);
    }

    @GetMapping("/profile")
    public ResponseEntity<TokenTimeProfileDTO> getTokenTimeProfileByQuery(@RequestParam Integer profileCode) {
        return tokenTimeService.findByProfile(profileCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/login/profile")
    public ResponseEntity<MappingJacksonValue> getTokenTimeProfile(@RequestParam Integer profileCode) {
        return tokenTimeService.findByProfileForLogin(profileCode)
                .map(dto -> {
                    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(dto);
                    mappingJacksonValue.setFilters(new SimpleFilterProvider().addFilter("loginDTOFilter", new LoginDTOFilter()));
                    return ResponseEntity.ok(mappingJacksonValue);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update")
    public ResponseEntity<TokenTimeProfileDTO> update(@RequestParam Integer profileCode, @Valid @RequestBody TokenTimeProfileDTO tokenTimeProfileDTO) {
        TokenTimeProfileDTO existingProfileDTO = tokenTimeService.getTokenTimeProfile(profileCode);
        TokenTimeProfile existingProfile = existingProfileDTO.toDomainModel();
        TokenTimeProfile updatedProfile = tokenTimeProfileDTO.updateDomainModel(existingProfile);
        TokenTimeProfileDTO result = tokenTimeService.update(profileCode, TokenTimeProfileDTO.fromDomainModel(updatedProfile));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/expiration")
    public ResponseEntity<Long> getExpirationTimeInMillis(@RequestParam Integer profileCode) {
        return ResponseEntity.ok(tokenTimeService.getExpirationTimeInMillis(Profile.fromCode(profileCode)));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByProfile(@RequestParam Integer profileCode) {
        return ResponseEntity.ok(tokenTimeService.existsByProfile(profileCode));
    }
}