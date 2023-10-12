package org.camilo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfigUtils {

    private final Properties properties;
    private final static AppConfigUtils INSTANCE = new AppConfigUtils();

    private AppConfigUtils() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AppConfigUtils getInstance() {
        return INSTANCE;
    }

    public String getPasswordClient() {
        return properties.getProperty("passwordClient");
    }

    public String getClientId() {
        return properties.getProperty("clientId");
    }

}
