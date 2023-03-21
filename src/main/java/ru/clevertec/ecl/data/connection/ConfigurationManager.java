package ru.clevertec.ecl.data.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationManager {
    private final Properties properties;
    private static final String propsFile = "/application.properties";

    public ConfigurationManager() {
        properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream(propsFile);) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
