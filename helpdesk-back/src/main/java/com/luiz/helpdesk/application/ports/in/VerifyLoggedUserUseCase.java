package com.luiz.helpdesk.application.ports.in;

import com.luiz.helpdesk.infrastructure.adapters.out.config.CustomUserDetails;

public interface VerifyLoggedUserUseCase {
    CustomUserDetails getAuthenticatedUser();
    String refreshToken(String oldToken);
}