package com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils;

import com.luiz.helpdesk.infrastructure.adapters.out.persistence.config.PersistenceConfig;

import java.util.function.Supplier;

public class RecursionControlWrapperUtil {

    public static <T> T executeWithRecursionControl(Supplier<T> operation) {
        return executeWithRecursionControl(operation, PersistenceConfig.getMaxRecursionDepth());
    }

    public static <T> T executeWithRecursionControl(Supplier<T> operation, int maxRecursionDepth) {
        try {
            PersistenceConfig.incrementRecursionDepth();
            if (PersistenceConfig.getRecursionDepth() > maxRecursionDepth) {
                throw new RuntimeException("Máxima profundidade de recursão atingida");
            }
            return operation.get();
        } finally {
            PersistenceConfig.decrementRecursionDepth();
        }
    }
}