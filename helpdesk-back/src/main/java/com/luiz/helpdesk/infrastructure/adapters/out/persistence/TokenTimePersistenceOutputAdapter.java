package com.luiz.helpdesk.infrastructure.adapters.out.persistence;

import com.luiz.helpdesk.application.ports.out.TokenTimePersistenceOutputPort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity.TokenTimeProfilesEntity;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.springdata.JpaTokenTimeRepository;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${root.token.expiration.time.minutes}")
    private BigDecimal rootTokenExpirationTimeMinutes;

    @Value("${root.time.to.show.dialog.minutes}")
    private BigDecimal rootTimeToShowDialogMinutes;

    @Value("${root.dialog.display.time.for.token.update.minutes}")
    private BigDecimal rootDialogDisplayTimeForTokenUpdateMinutes;

    @Value("${root.token.update.interval.minutes}")
    private BigDecimal rootTokenUpdateIntervalMinutes;

    public TokenTimePersistenceOutputAdapter(JpaTokenTimeRepository jpaTokenTimeRepository) {
        this.jpaTokenTimeRepository = jpaTokenTimeRepository;
    }

    @Override
    @Transactional
    public TokenTimeProfile saveTokenTime(TokenTimeProfile tokenTimeProfile) {
        if (tokenTimeProfile.getProfile() == Profile.ROOT) {
            throw new IllegalArgumentException("Cannot save ROOT profile");
        }
        TokenTimeProfilesEntity entity = TokenTimeProfilesEntity.fromDomainModel(tokenTimeProfile);
        TokenTimeProfilesEntity savedEntity = jpaTokenTimeRepository.save(entity);
        return savedEntity.toDomainModel();
    }

    @Override
    public Optional<TokenTimeProfile> findByProfile(Integer profileCode) {
        if (profileCode == null) {
            throw new IllegalArgumentException("Profile code cannot be null");
        }
        Profile profile = Profile.fromCode(profileCode);
        return jpaTokenTimeRepository.findByProfile(profile.getCode())
                .map(TokenTimeProfilesEntity::toDomainModel);
    }

    @Override
    public Optional<TokenTimeProfile> findByProfileForLogin(Integer profileCode) {
        if (profileCode == null) {
            throw new IllegalArgumentException("Profile code cannot be null");
        }
        Profile profile = Profile.fromCode(profileCode);
        if (profile == Profile.ROOT) {
            return Optional.of(createRootTokenTimeProfile());
        }
        return findByProfile(profileCode);
    }

    @Override
    public List<TokenTimeProfile> findAllTokenTime() {
        return jpaTokenTimeRepository.findAll().stream()
                .map(TokenTimeProfilesEntity::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByProfile(Integer profileCode) {
        if (profileCode == null) {
            throw new IllegalArgumentException("Profile code cannot be null");
        }
        Profile profile = Profile.fromCode(profileCode);
        return jpaTokenTimeRepository.existsByProfile(profile.getCode());
    }

    @Override
    @Transactional
    public Optional<TokenTimeProfile> updateTokenTime(Integer profileCode, TokenTimeProfile tokenTimeProfile) {
        if (profileCode == null) {
            throw new IllegalArgumentException("Profile code cannot be null");
        }
        Profile profile = Profile.fromCode(profileCode);
        if (profile == Profile.ROOT) {
            throw new IllegalArgumentException("Cannot update ROOT profile");
        }
        return jpaTokenTimeRepository.findByProfile(profile.getCode())
                .map(existingEntity -> {
                    existingEntity.updateFromDomainModel(tokenTimeProfile);
                    TokenTimeProfilesEntity updatedEntity = jpaTokenTimeRepository.save(existingEntity);
                    return updatedEntity.toDomainModel();
                });
    }

    @Override
    public long getTokenExpirationTimeInMillis(Profile profile) {
        if (profile == Profile.ROOT) {
            return convertToMillis(rootTokenExpirationTimeMinutes).longValue();
        }
        return jpaTokenTimeRepository.findByProfile(profile.getCode())
                .map(entity -> convertToMillis(entity.getTokenExpirationTimeMinutes()).longValue())
                .orElse(0L);
    }

    private BigDecimal convertToMillis(BigDecimal minutes) {
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

    private TokenTimeProfile createRootTokenTimeProfile() {
        return TokenTimeProfilesEntity.builder()
                .withProfile(Profile.ROOT)
                .withTokenExpirationTimeMinutes(rootTokenExpirationTimeMinutes)
                .withTimeToShowDialogMinutes(rootTimeToShowDialogMinutes)
                .withDialogDisplayTimeForTokenUpdateMinutes(rootDialogDisplayTimeForTokenUpdateMinutes)
                .withTokenUpdateIntervalMinutes(rootTokenUpdateIntervalMinutes)
                .build()
                .toDomainModel();
    }
}