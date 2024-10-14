package com.luiz.helpdesk.infrastructure.adapters.out.config.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.luiz.helpdesk.infrastructure.adapters.in.web.annotation.ExcludeFromLogin;

public class LoginDTOFilter extends SimpleBeanPropertyFilter {
    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (include(writer)) {
            writer.serializeAsField(pojo, jgen, provider);
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }

    protected boolean include(PropertyWriter writer) {
        return writer.getAnnotation(ExcludeFromLogin.class) == null;
    }
}