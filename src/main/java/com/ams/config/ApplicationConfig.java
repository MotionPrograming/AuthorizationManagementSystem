package com.ams.config;

import java.io.InputStream;
import java.util.Properties;

public final class ApplicationConfig {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ApplicationConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException("application.properties not found");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Application configuration loading failed", e);
        }
    }

    private ApplicationConfig() {
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new RuntimeException("Missing application property: " + key);
        }

        return value;
    }

}