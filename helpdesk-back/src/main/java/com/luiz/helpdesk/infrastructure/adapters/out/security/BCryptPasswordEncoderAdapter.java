package com.luiz.helpdesk.infrastructure.adapters.out.security;

import com.luiz.helpdesk.application.ports.out.PasswordEncoderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private static final Logger logger = LoggerFactory.getLogger(BCryptPasswordEncoderAdapter.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public BCryptPasswordEncoderAdapter(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        logger.info("BCryptPasswordEncoderAdapter initialized");
    }

    @Override
    public String encode(String rawPassword) {
        logger.debug("Encoding password");
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        logger.debug("Password encoded successfully. Length: {}", encodedPassword.length());
        return encodedPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        logger.debug("Matching password");
        boolean isMatch = bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
        logger.debug("Password match result: {}", isMatch);
        return isMatch;
    }

    @PostConstruct
    public void testPasswordEncoder() {
        String rawPassword = "L@ndQLYN5yvx";
        String storedHash = "$2a$10$CH5zmL3l./Q.NFBGpZvHyusb558ddz7FiJ9nNMwY3zaU1rUVj911.";
        // Teste com o adaptador
        boolean adapterMatches = this.matches(rawPassword, storedHash);
        logger.debug("Adapter match test: {}", adapterMatches);
        // Teste direto com BCryptPasswordEncoder
        boolean encoderMatches = bCryptPasswordEncoder.matches(rawPassword, storedHash);
        logger.debug("Direct BCryptPasswordEncoder match test: {}", encoderMatches);
    }
}