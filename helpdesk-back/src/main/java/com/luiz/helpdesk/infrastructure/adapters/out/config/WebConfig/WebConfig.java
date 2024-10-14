package com.luiz.helpdesk.infrastructure.adapters.out.config.WebConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.luiz.helpdesk.infrastructure.adapters.out.config.serialization.LoginDTOFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .map(converter -> (MappingJackson2HttpMessageConverter) converter)
                .forEach(converter -> {
                    ObjectMapper mapper = converter.getObjectMapper();
                    SimpleFilterProvider filterProvider = new SimpleFilterProvider();
                    filterProvider.addFilter("loginDTOFilter", new LoginDTOFilter());
                    mapper.setFilterProvider(filterProvider);
                });
    }
}