package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.out.DecryptionPort;
import com.luiz.helpdesk.infrastructure.adapters.out.config.security.DecryptionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecryptionService implements DecryptionPort {

    private final DecryptionAdapter decryptionAdapter;

    @Autowired
    public DecryptionService(DecryptionAdapter decryptionAdapter) {
        this.decryptionAdapter = decryptionAdapter;
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        return decryptionAdapter.decrypt(encryptedData);
    }
}