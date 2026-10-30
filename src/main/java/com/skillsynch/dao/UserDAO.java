
package com.skillsynch.dao;

import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User findByUsername(String username) throws Exception {

        String sql = """
                SELECT user_id, username, password_hash,
                       role, student_id, company_id
                FROM Users
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    int studentValue = result.getInt("student_id");
                    Integer studentId = result.wasNull()
                            ? null : studentValue;

                    int companyValue = result.getInt("company_id");
                    Integer companyId = result.wasNull()
                            ? null : companyValue;

                    return new User(
                            result.getInt("user_id"),
                            result.getString("username"),
                            result.getString("password_hash"),
                            result.getString("role"),
                            studentId,
                            companyId
                    );
                }
            }
        }

        return null;
    }
}
