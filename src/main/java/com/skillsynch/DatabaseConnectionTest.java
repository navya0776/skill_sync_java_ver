
package com.skillsynch;

import com.skillsynch.config.DatabaseConfig;
import java.sql.Connection;

public class DatabaseConnectionTest {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConfig.getConnection()) {

            System.out.println("SkillSynch Database Connected!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Connection Status: " + !connection.isClosed());

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
