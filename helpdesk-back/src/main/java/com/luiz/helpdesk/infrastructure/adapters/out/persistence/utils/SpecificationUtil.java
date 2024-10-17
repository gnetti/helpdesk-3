package com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.function.BiFunction;

public class SpecificationUtil {

    private SpecificationUtil() {
        throw new IllegalStateException("Classe utilitária");
    }

    @FunctionalInterface
    public interface FilterOperation {
        Predicate buildPredicate(Root<?> root, CriteriaBuilder criteriaBuilder, String value);
    }

    public static class FilterOperations {
        public static FilterOperation likeIgnoreCase(String fieldName) {
            return (root, criteriaBuilder, value) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%");
        }

        public static FilterOperation like(String fieldName) {
            return (root, criteriaBuilder, value) ->
                    criteriaBuilder.like(root.get(fieldName), "%" + value + "%");
        }

        public static FilterOperation dateEqual(String fieldName) {
            return (root, criteriaBuilder, value) -> {
                try {
                    LocalDate date = LocalDate.parse(value);
                    return criteriaBuilder.equal(root.get(fieldName), date);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Formato de data inválido para " + fieldName + ": " + value, e);
                }
            };
        }

        public static FilterOperation dateGreaterThanOrEqualTo(String fieldName) {
            return (root, criteriaBuilder, value) -> {
                try {
                    LocalDate date = LocalDate.parse(value);
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), date);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Formato de data inválido para " + fieldName + ": " + value, e);
                }
            };
        }

        public static FilterOperation dateLessThanOrEqualTo(String fieldName) {
            return (root, criteriaBuilder, value) -> {
                try {
                    LocalDate date = LocalDate.parse(value);
                    return criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), date);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Formato de data inválido para " + fieldName + ": " + value, e);
                }
            };
        }

        public static FilterOperation in(String fieldName) {
            return (root, criteriaBuilder, value) -> {
                String[] values = value.split(",");
                return root.get(fieldName).in((Object[]) values);
            };
        }
    }

    public static <T> Specification<T> createSpecification(Map<String, String> filters, Map<String, BiFunction<String, Object, FilterOperation>> filterOperations) {
        Specification<T> spec = Specification.where(null);
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty() && filterOperations.containsKey(key)) {
                FilterOperation operation = filterOperations.get(key).apply(key, value);
                spec = spec.and((root, query, criteriaBuilder) -> operation.buildPredicate(root, criteriaBuilder, value));
            }
        }
        return spec;
    }

    public static Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        return Sort.by(direction, sortBy.split(","));
    }
}