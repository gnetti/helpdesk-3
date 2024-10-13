package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.TokenTimeManagementUseCasePort;
import com.luiz.helpdesk.application.ports.out.TokenTimePersistenceOutputPort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.profile.ProfileNotFoundException;
import com.luiz.helpdesk.domain.exception.tokenTime.DuplicateProfileException;
import com.luiz.helpdesk.domain.exception.tokenTime.TokenTimeUpdateException;
import com.luiz.helpdesk.domain.exception.tokenTime.UnauthorizedAccessException;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.TokenTimeProfileDTO;
import com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils.TokenTimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TokenTimeManageService implements TokenTimeManagementUseCasePort {

    private final TokenTimePersistenceOutputPort tokenTimeRepository;

    public TokenTimeManageService(TokenTimePersistenceOutputPort tokenTimeRepository) {
        this.tokenTimeRepository = tokenTimeRepository;
    }

    @Override
    @Transactional
    public TokenTimeProfileDTO create(TokenTimeProfileDTO tokenTimeProfileDTO) throws UnauthorizedAccessException, IllegalArgumentException, DuplicateProfileException {
        TokenTimeUtil.validateForCreate(
                tokenTimeRepository::isRootUser,
                tokenTimeProfileDTO,
                () -> tokenTimeRepository.existsByProfile(tokenTimeProfileDTO.getProfileCode())
        );
        return TokenTimeProfileDTO.fromDomainModel(tokenTimeRepository.saveTokenTime(tokenTimeProfileDTO.toDomainModel()));
    }

    @Override
    @Transactional
    public TokenTimeProfileDTO update(Integer profileCode, TokenTimeProfileDTO tokenTimeProfileDTO) throws UnauthorizedAccessException, IllegalArgumentException, ProfileNotFoundException, TokenTimeUpdateException {
        TokenTimeUtil.validateForUpdate(
                tokenTimeRepository::isRootUser,
                profileCode,
                () -> tokenTimeRepository.existsByProfile(profileCode)
        );
        return tokenTimeRepository.updateTokenTime(profileCode, tokenTimeProfileDTO.toDomainModel())
                .map(updatedProfile -> TokenTimeUtil.handleUpdate(profileCode, tokenTimeProfileDTO, updatedProfile))
                .orElseThrow(() -> new TokenTimeUpdateException("Falha ao atualizar TokenTimeProfile para perfil: " + Profile.fromCode(profileCode)));
    }

    @Override
    public Optional<TokenTimeProfileDTO> findByProfile(Integer profileCode) throws UnauthorizedAccessException {
        TokenTimeUtil.validateRootAccess(tokenTimeRepository::isRootUser);
        return tokenTimeRepository.findByProfile(profileCode).map(TokenTimeProfileDTO::fromDomainModel);
    }

    @Override
    public Optional<TokenTimeProfileDTO> findByProfileForLogin(Integer profileCode) {
        return tokenTimeRepository.findByProfileForLogin(profileCode).map(TokenTimeProfileDTO::fromDomainModel);
    }

    @Override
    public List<TokenTimeProfileDTO> findAll() throws UnauthorizedAccessException {
        TokenTimeUtil.validateRootAccess(tokenTimeRepository::isRootUser);
        return tokenTimeRepository.findAllTokenTime().stream()
                .map(TokenTimeProfileDTO::fromDomainModel)
                .toList();
    }

    @Override
    public boolean existsByProfile(Integer profileCode) {
        return tokenTimeRepository.existsByProfile(profileCode);
    }

    @Override
    public long getExpirationTimeInMillis(Profile profile) {
        return tokenTimeRepository.getTokenExpirationTimeInMillis(profile);
    }

    @Override
    public TokenTimeProfileDTO getTokenTimeProfile(Integer profileCode) throws TokenTimeUpdateException {
        return tokenTimeRepository.findByProfile(profileCode)
                .map(TokenTimeProfileDTO::fromDomainModel)
                .orElseThrow(() -> new TokenTimeUpdateException("TokenTimeProfile não encontrado para perfil: " + Profile.fromCode(profileCode)));
    }
}