package com.luiz.helpdesk.infrastructure.adapters.out.persistence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {

    private static int maxRecursionDepth;

    @Value("${persistence.max-recursion-depth}")
    public void setMaxRecursionDepth(int depth) {
        PersistenceConfig.maxRecursionDepth = depth;
    }

    private static final ThreadLocal<Integer> recursionDepth = new ThreadLocal<>();

    public static int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public static void incrementRecursionDepth() {
        recursionDepth.set(getRecursionDepth() + 1);
    }

    public static void decrementRecursionDepth() {
        recursionDepth.set(getRecursionDepth() - 1);
    }

    public static int getRecursionDepth() {
        return recursionDepth.get() == null ? 0 : recursionDepth.get();
    }
}