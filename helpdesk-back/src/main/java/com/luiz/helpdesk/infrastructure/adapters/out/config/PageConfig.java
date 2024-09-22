package com.luiz.helpdesk.infrastructure.adapters.out.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.luiz.helpdesk.application.ports.out.PaginationOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.io.IOException;

@Configuration
public class PageConfig {

    private final PaginationOutputPort paginationService;

    public PageConfig(PaginationOutputPort paginationService) {
        this.paginationService = paginationService;
        System.out.println("PageConfig created with PaginationService");
    }

    public enum PageSerializationMode {
        VIA_DTO,
        VIA_ENTITY
    }

    @Bean
    public Module customPageModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(pageClass(), new CustomPageSerializer(pageSerializationMode(), paginationService));
        System.out.println("Custom Page Module created with serialization mode: " + pageSerializationMode());
        return module;
    }

    @SuppressWarnings("unchecked")
    private Class<Page<?>> pageClass() {
        return (Class<Page<?>>) (Class<?>) Page.class;
    }

    @Bean
    public PageSerializationMode pageSerializationMode() {
        return PageSerializationMode.VIA_DTO;
    }

    static class CustomPageSerializer extends JsonSerializer<Page<?>> {
        private final PageSerializationMode mode;
        private final PaginationOutputPort paginationService;

        public CustomPageSerializer(PageSerializationMode mode, PaginationOutputPort paginationService) {
            this.mode = mode;
            this.paginationService = paginationService;
            System.out.println("CustomPageSerializer created with mode: " + mode);
        }

        @Override
        public void serialize(Page<?> page, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            System.out.println("Serializing Page: number=" + page.getNumber() + ", size=" + page.getSize() +
                    ", totalElements=" + page.getTotalElements() + ", totalPages=" + page.getTotalPages());

            gen.writeStartObject();
            gen.writeObjectField("content", page.getContent());
            gen.writeNumberField("pageNumber", page.getNumber());
            gen.writeNumberField("pageSize", page.getSize());
            gen.writeNumberField("totalElements", page.getTotalElements());
            gen.writeNumberField("totalPages", page.getTotalPages());
            if (mode == PageSerializationMode.VIA_DTO) {
                gen.writeBooleanField("first", page.isFirst());
                gen.writeBooleanField("last", page.isLast());
                gen.writeNumberField("numberOfElements", page.getNumberOfElements());
            }
            gen.writeNumberField("minPageSize", paginationService.getMinPageSize());
            gen.writeNumberField("maxPageSize", paginationService.getMaxPageSize());
            gen.writeNumberField("defaultPage", paginationService.getDefaultPage());
            gen.writeNumberField("defaultSize", paginationService.getDefaultSize());
            gen.writeEndObject();

        }
    }
}