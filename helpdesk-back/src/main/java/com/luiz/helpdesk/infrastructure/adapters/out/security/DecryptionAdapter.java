package com.luiz.helpdesk.infrastructure.adapters.out.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class DecryptionAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DecryptionAdapter.class);

    @Value("${crypto.secretKey}")
    private String base64SecretKey;

    @Value("${crypto.secretIv}")
    private String base64SecretIv;

    private final Environment environment;

    public DecryptionAdapter(Environment environment) {
        this.environment = environment;
    }

    private SecretKeySpec getKeySpec() {
        logger.debug("Generating SecretKeySpec");
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        logger.debug("Decoded key length: {}", keyBytes.length);
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            String errorMessage = "Invalid key length: " + keyBytes.length;
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        logger.debug("SecretKeySpec generated successfully");
        return new SecretKeySpec(keyBytes, "AES");
    }

    private IvParameterSpec getIvSpec() {
        logger.debug("Generating IvParameterSpec");
        byte[] ivBytes = Base64.getDecoder().decode(base64SecretIv);
        logger.debug("Decoded IV length: {}", ivBytes.length);
        if (ivBytes.length != 16) {
            String errorMessage = "Invalid IV length: " + ivBytes.length;
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        logger.debug("IvParameterSpec generated successfully");
        return new IvParameterSpec(ivBytes);
    }

    public String decrypt(String encryptedData) throws Exception {
        logger.info("Starting decryption process");
        try {
            logger.debug("Decoding encrypted data");
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            logger.debug("Encrypted data length: {}", encryptedBytes.length);

            logger.debug("Initializing Cipher for decryption");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKeySpec(), getIvSpec());

            logger.debug("Decrypting data");
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            logger.debug("Decrypted data length: {}", decryptedBytes.length);

            String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);
            logger.debug("Decrypted string length: {}", decryptedString.length());

            // ATENÇÃO: Isso loga a senha completa. Use apenas em ambiente de teste controlado!
            logger.warn("SECURITY RISK: Logging full decrypted string. This should NEVER be done in production!");
            logger.debug("Decrypted string (FULL): {}", decryptedString);

            logger.info("Decryption process completed successfully");
            return decryptedString;
        } catch (Exception e) {
            logger.error("Error during decryption process", e);
            throw new Exception("Error decrypting data", e);
        }
    }
}