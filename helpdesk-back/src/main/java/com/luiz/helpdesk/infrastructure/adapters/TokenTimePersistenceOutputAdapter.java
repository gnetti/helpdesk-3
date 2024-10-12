package com.luiz.helpdesk.infrastructure.adapters;

import com.luiz.helpdesk.application.ports.out.TokenTimePersistenceOutputPort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity.TokenTimeProfilesEntity;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.springdata.JpaTokenTimeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TokenTimePersistenceOutputAdapter implements TokenTimePersistenceOutputPort {

    private final JpaTokenTimeRepository jpaTokenTimeRepository;

    public TokenTimePersistenceOutputAdapter(JpaTokenTimeRepository jpaTokenTimeRepository) {
        this.jpaTokenTimeRepository = jpaTokenTimeRepository;
    }

    @Override
    @Transactional
    public TokenTimeProfile saveTokenTime(TokenTimeProfile tokenTimeProfile) {
        TokenTimeProfilesEntity entity = TokenTimeProfilesEntity.fromDomainModel(tokenTimeProfile);
        TokenTimeProfilesEntity savedEntity = jpaTokenTimeRepository.save(entity);
        return convertToMilliseconds(savedEntity.toDomainModel());
    }

    @Override
    public Optional<TokenTimeProfile> findByProfile(Profile profile) {
        if (profile == Profile.ROOT) {
            return Optional.empty();
        }
        return jpaTokenTimeRepository.findByProfile(profile.getCode())
                .map(TokenTimeProfilesEntity::toDomainModel)
                .map(this::convertToMilliseconds);
    }

    @Override
    public List<TokenTimeProfile> findAllTokenTime() {
        return jpaTokenTimeRepository.findAll().stream()
                .map(TokenTimeProfilesEntity::toDomainModel)
                .map(this::convertToMilliseconds)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByProfile(Profile profile) {
        return profile == Profile.ROOT || jpaTokenTimeRepository.existsByProfile(profile.getCode());
    }

    @Override
    @Transactional
    public Optional<TokenTimeProfile> updateTokenTime(Profile profile, TokenTimeProfile tokenTimeProfile) {
        if (profile == Profile.ROOT) {
            return Optional.empty();
        }
        return jpaTokenTimeRepository.findByProfile(profile.getCode())
                .map(existingEntity -> {
                    existingEntity.updateFromDomainModel(tokenTimeProfile);
                    TokenTimeProfilesEntity updatedEntity = jpaTokenTimeRepository.save(existingEntity);
                    return convertToMilliseconds(updatedEntity.toDomainModel());
                });
    }

    public TokenTimeProfile convertToMilliseconds(TokenTimeProfile profile) {
        return TokenTimeProfile.builder()
                .withProfile(profile.getProfile())
                .withTokenExpirationTimeMinutes(convertToMillis(profile.getTokenExpirationTimeMinutes()))
                .withTimeToShowDialogMinutes(convertToMillis(profile.getTimeToShowDialogMinutes()))
                .withDialogDisplayTimeForTokenUpdateMinutes(convertToMillis(profile.getDialogDisplayTimeForTokenUpdateMinutes()))
                .withTokenUpdateIntervalMinutes(convertToMillis(profile.getTokenUpdateIntervalMinutes()))
                .build();
    }

    public BigDecimal convertToMillis(BigDecimal minutes) {
        return minutes.multiply(BigDecimal.valueOf(60000));
    }

    @Override
    public boolean isRootUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getProfile() != null &&
                    Profile.ROOT.getCode().equals(customUserDetails.getProfile());
        }
        return false;
    }
}