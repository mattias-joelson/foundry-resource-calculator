package org.joelson.mattias.foundry.resource_calculator.util;

public class StringUtil {

    private StringUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static String requireNullOrNotEmpty(String s, String message) {
        if (s != null && s.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return s;
    }
    public static String requireNotNullAndNotEmpty(String s, String message) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return s;
    }

    public static String requireNotNull(String s, String message) {
        if (s == null) {
            throw new IllegalArgumentException(message);
        }
        return s;
    }
}
