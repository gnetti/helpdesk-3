package com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PersonFilterOperationsUtil {

    private static final Map<String, BiFunction<String, Object, SpecificationUtil.FilterOperation>> FILTER_OPERATIONS = new HashMap<>();

    static {
        FILTER_OPERATIONS.put("name", (field, value) -> SpecificationUtil.FilterOperations.likeIgnoreCase(field));
        FILTER_OPERATIONS.put("cpf", (field, value) -> SpecificationUtil.FilterOperations.like(field));
        FILTER_OPERATIONS.put("email", (field, value) -> SpecificationUtil.FilterOperations.likeIgnoreCase(field));
        FILTER_OPERATIONS.put("profile", (field, value) -> criarFiltroDePerfilProfissional());
        FILTER_OPERATIONS.put("creationDate", (field, value) -> SpecificationUtil.FilterOperations.dateEqual(field));
        FILTER_OPERATIONS.put("creationDateFrom", (field, value) -> SpecificationUtil.FilterOperations.dateGreaterThanOrEqualTo("creationDate"));
        FILTER_OPERATIONS.put("creationDateTo", (field, value) -> SpecificationUtil.FilterOperations.dateLessThanOrEqualTo("creationDate"));
        FILTER_OPERATIONS.put("theme", (field, value) -> criarFiltroDeTema());
        FILTER_OPERATIONS.put("id", (field, value) -> criarFiltroNumerico());
    }

    public static Map<String, BiFunction<String, Object, SpecificationUtil.FilterOperation>> getFilterOperations() {
        return new HashMap<>(FILTER_OPERATIONS);
    }

    private static SpecificationUtil.FilterOperation criarFiltroDePerfilProfissional() {
        return (root, criteriaBuilder, value) -> {
            try {
                String[] profiles = value.split(",");
                return root.join("profiles").in((Object[]) profiles);
            } catch (Exception e) {
                throw new IllegalArgumentException("Valor inválido para perfil profissional: " + value, e);
            }
        };
    }

    private static SpecificationUtil.FilterOperation criarFiltroDeTema() {
        return (root, criteriaBuilder, value) -> {
            try {
                String[] themes = value.split(",");
                return root.join("themes").in((Object[]) themes);
            } catch (Exception e) {
                throw new IllegalArgumentException("Valor inválido para tema: " + value, e);
            }
        };
    }

    private static SpecificationUtil.FilterOperation criarFiltroNumerico() {
        return (root, criteriaBuilder, value) -> {
            try {
                Long numericValue = Long.parseLong(value);
                return criteriaBuilder.equal(root.get("id"), numericValue);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor numérico inválido para id: " + value, e);
            }
        };
    }
}