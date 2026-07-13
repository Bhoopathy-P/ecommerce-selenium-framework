package com.ecommerce.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads framework configuration from src/test/resources/config.properties.
 * <p>
 * Any property can be overridden at runtime via a JVM system property, e.g.:
 * {@code mvn test -Dbrowser=firefox -Dbase.url=https://automationexercise.com}
 * <p>
 * This class is a lazy-loaded singleton so the properties file is read only once
 * per test run.
 */
public final class ConfigReader {

    private static final String CONFIG_PATH = "src/test/resources/config.properties";
    private static Properties properties;

    private ConfigReader() {
        // utility class - no instantiation
    }

    private static synchronized void load() {
        if (properties != null) {
            return;
        }
        properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load config.properties from " + CONFIG_PATH, e);
        }
    }

    /**
     * Returns the value for the given key, giving precedence to a JVM system
     * property of the same name if one was supplied (e.g. -Dbrowser=firefox).
     */
    public static String get(String key) {
        load();
        String systemOverride = System.getProperty(key);
        if (systemOverride != null && !systemOverride.isBlank()) {
            return systemOverride;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing config property: " + key);
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        load();
        String systemOverride = System.getProperty(key);
        if (systemOverride != null && !systemOverride.isBlank()) {
            return systemOverride;
        }
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String browser() {
        return get("browser");
    }
}
