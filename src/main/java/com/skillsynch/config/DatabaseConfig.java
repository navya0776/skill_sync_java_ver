
package com.skillsynch.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConfig {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class
                .getResourceAsStream("/db-credentials.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "Database credentials file not found!"
                );
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not load database configuration.", e
            );
        }
    }

    public static Connection getConnection() throws Exception {

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }
}
