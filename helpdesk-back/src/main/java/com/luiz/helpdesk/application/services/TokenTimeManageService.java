package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.TokenTimeManagementUseCasePort;
import com.luiz.helpdesk.application.ports.out.TokenTimePersistenceOutputPort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.tokenTime.TokenTimeUpdateException;
import com.luiz.helpdesk.domain.validator.TokenTimeValidator;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.TokenTimeProfileDTO;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.TokenTimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenTimeManageService implements TokenTimeManagementUseCasePort {

    private final TokenTimePersistenceOutputPort tokenTimeRepository;

    @Value("${root.token.expiration.time.minutes}")
    private BigDecimal rootTokenExpirationTimeMinutes;

    @Value("${root.time.to.show.dialog.minutes}")
    private BigDecimal rootTimeToShowDialogMinutes;

    @Value("${root.dialog.display.time.for.token.update.minutes}")
    private BigDecimal rootDialogDisplayTimeForTokenUpdateMinutes;

    @Value("${root.token.update.interval.minutes}")
    private BigDecimal rootTokenUpdateIntervalMinutes;

    public TokenTimeManageService(TokenTimePersistenceOutputPort tokenTimeRepository) {
        this.tokenTimeRepository = tokenTimeRepository;
    }

    @Override
    @Transactional
    public TokenTimeProfileDTO create(TokenTimeProfileDTO tokenTimeProfileDTO) {
        TokenTimeValidator.validateRootAccess(tokenTimeRepository.isRootUser());
        TokenTimeValidator.validateNotRootProfile(tokenTimeProfileDTO.getProfile());
        TokenTimeValidator.validateUniqueProfile(tokenTimeRepository.existsByProfile(tokenTimeProfileDTO.getProfile()), tokenTimeProfileDTO.getProfile());
        return TokenTimeProfileDTO.fromDomainModel(tokenTimeRepository.saveTokenTime(tokenTimeProfileDTO.toDomainModel()));
    }

    @Override
    @Transactional
    public TokenTimeProfileDTO update(Profile profile, TokenTimeProfileDTO tokenTimeProfileDTO) {
        TokenTimeValidator.validateRootAccess(tokenTimeRepository.isRootUser());
        TokenTimeValidator.validateNotRootProfile(profile);
        TokenTimeValidator.validateProfileExists(tokenTimeRepository.existsByProfile(profile), profile);
        return tokenTimeRepository.updateTokenTime(profile, tokenTimeProfileDTO.toDomainModel())
                .map(TokenTimeProfileDTO::fromDomainModel)
                .orElseThrow(() -> new TokenTimeUpdateException("Failed to update TokenTimeProfile for profile: " + profile));
    }

    @Override
    public Optional<TokenTimeProfileDTO> findByProfile(Profile profile) {
        TokenTimeValidator.validateRootAccess(tokenTimeRepository.isRootUser());
        if (profile == Profile.ROOT) {
            return Optional.of(createRootTokenTimeProfileDTO());
        }
        return tokenTimeRepository.findByProfile(profile).map(TokenTimeProfileDTO::fromDomainModel);
    }

    @Override
    public List<TokenTimeProfileDTO> findAll() {
        TokenTimeValidator.validateRootAccess(tokenTimeRepository.isRootUser());
        List<TokenTimeProfileDTO> profiles = tokenTimeRepository.findAllTokenTime().stream()
                .filter(profile -> profile.getProfile() != Profile.ROOT)
                .map(TokenTimeProfileDTO::fromDomainModel)
                .collect(Collectors.toList());
        profiles.add(createRootTokenTimeProfileDTO());
        return profiles;
    }

    @Override
    public boolean existsByProfile(Profile profile) {
        return profile == Profile.ROOT || tokenTimeRepository.existsByProfile(profile);
    }

    @Override
    public long getExpirationTimeInMillis(Profile profile) {
        return TokenTimeUtil.getTokenExpirationTimeMillis(profile,
                tokenTimeRepository, rootTokenExpirationTimeMinutes).longValue();
    }

    @Override
    public TokenTimeProfileDTO getTokenTimeProfile(Profile profile) {
        if (profile == Profile.ROOT) {
            return createRootTokenTimeProfileDTO();
        }
        return tokenTimeRepository.findByProfile(profile)
                .map(TokenTimeProfileDTO::fromDomainModel)
                .orElseThrow(() -> new TokenTimeUpdateException("TokenTimeProfile not found for profile: " + profile));
    }

    private TokenTimeProfileDTO createRootTokenTimeProfileDTO() {
        return TokenTimeUtil.createRootTokenTimeProfileDTO(
                tokenTimeRepository,
                rootTokenExpirationTimeMinutes,
                rootTimeToShowDialogMinutes,
                rootDialogDisplayTimeForTokenUpdateMinutes,
                rootTokenUpdateIntervalMinutes
        );
    }
}