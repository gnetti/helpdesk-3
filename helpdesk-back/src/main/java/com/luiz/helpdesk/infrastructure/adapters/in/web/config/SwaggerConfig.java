package com.luiz.helpdesk.infrastructure.adapters.in.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI helpdeskOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Helpdesk API 3.0")
                        .description("API for helpdesk system 3.0")
                        .version("v3.0.0")
                        .contact(new Contact()
                                .name("Luiz Generoso")
                                .email("luizerytre@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));

    }
}