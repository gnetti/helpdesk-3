package com.luiz.helpdesk.infrastructure.adapters.in.web.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Get paginated results", description = "Retrieves a paginated list of items")
@Parameter(name = "page", description = "Page number (0-based)", schema = @Schema(type = "integer"))
@Parameter(name = "size", description = "Number of items per page", schema = @Schema(type = "integer"))
public @interface PaginationParameters {
}