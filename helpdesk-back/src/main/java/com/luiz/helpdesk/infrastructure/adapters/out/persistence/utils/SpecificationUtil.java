package com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpecificationUtil {

    private SpecificationUtil() {
        throw new IllegalStateException("Classe utilitária");
    }

    public static <T> Specification<T> createSpecificationFromFilters(Map<String, String> filters, Map<String, FilterOperation> filterOperations) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filters.forEach((key, value) -> {
                if (value != null && !value.isEmpty() && filterOperations.containsKey(key)) {
                    FilterOperation operation = filterOperations.get(key);
                    try {
                        predicates.add(operation.buildPredicate(root, criteriaBuilder, value));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Erro ao aplicar filtro para " + key + ": " + e.getMessage());
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
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

        public static FilterOperation equalIgnoreCase(String fieldName) {
            return (root, criteriaBuilder, value) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get(fieldName)), value.toLowerCase());
        }

        public static FilterOperation equal(String fieldName) {
            return (root, criteriaBuilder, value) ->
                    criteriaBuilder.equal(root.get(fieldName), value);
        }

        public static FilterOperation greaterThanOrEqualTo(String fieldName) {
            return (root, criteriaBuilder, value) -> {
                try {
                    Number numericValue = NumberFormat.getInstance().parse(value);
                    return criteriaBuilder.ge(root.get(fieldName), numericValue);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Valor numérico inválido para " + fieldName + ": " + value, e);
                }
            };
        }

        public static FilterOperation lessThanOrEqualTo(String fieldName) {
            return (root, criteriaBuilder, value) -> {
                try {
                    Number numericValue = NumberFormat.getInstance().parse(value);
                    return criteriaBuilder.le(root.get(fieldName), numericValue);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Valor numérico inválido para " + fieldName + ": " + value, e);
                }
            };
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

        public static FilterOperation numericEqual(String fieldName) {
            return (root, criteriaBuilder, value) -> {
                try {
                    Number numericValue = NumberFormat.getInstance().parse(value);
                    return criteriaBuilder.equal(root.get(fieldName), numericValue);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Valor numérico inválido para " + fieldName + ": " + value, e);
                }
            };
        }
    }
}