package com.luiz.helpdesk.infrastructure.adapters.out.config;

import com.luiz.helpdesk.application.ports.in.InitializeDatabaseUseCasePort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitializationConfig {

    @Bean
    public CommandLineRunner initDatabase(InitializeDatabaseUseCasePort initializeDatabaseUseCase) {
        return args -> {
            initializeDatabaseUseCase.initializeDatabase();
        };
    }
}
