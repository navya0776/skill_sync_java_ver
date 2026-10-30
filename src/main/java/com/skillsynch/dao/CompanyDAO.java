
package com.skillsynch.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompanyDAO {

    public int createCompany(
            Connection connection,
            String companyName,
            String industry,
            String description
    ) throws Exception {

        String sql = """
                INSERT INTO Companies
                (company_name, industry, description)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, companyName);
            statement.setString(2, industry);
            statement.setString(3, description);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new Exception("Company creation failed.");
    }
}
