package com.luiz.helpdesk.application.ports.out;

public interface DecryptionPort {
    String decrypt(String encryptedData) throws Exception;
}