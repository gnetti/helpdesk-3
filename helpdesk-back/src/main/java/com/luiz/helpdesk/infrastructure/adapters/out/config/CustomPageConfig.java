package com.luiz.helpdesk.infrastructure.adapters.out.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.luiz.helpdesk.application.ports.out.PaginationOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

@Configuration
public class CustomPageConfig {

    private final PaginationOutputPort paginationService;

    public CustomPageConfig(PaginationOutputPort paginationService) {
        this.paginationService = paginationService;
        System.out.println("CustomPageConfig created with PaginationService");
    }

    @Bean
    public Module customPageModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(pageClass(), new PageConfig.CustomPageSerializer(pageSerializationMode(), paginationService));
        System.out.println("Custom Page Module created with serialization mode: " + pageSerializationMode());
        return module;
    }

    @SuppressWarnings("unchecked")
    private Class<Page<?>> pageClass() {
        return (Class<Page<?>>) (Class<?>) Page.class;
    }

    private PageConfig.PageSerializationMode pageSerializationMode() {
        return PageConfig.PageSerializationMode.VIA_DTO;
    }
}