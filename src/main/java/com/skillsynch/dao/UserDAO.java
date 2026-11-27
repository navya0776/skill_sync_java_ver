
package com.skillsynch.dao;

import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.model.User;
import com.skillsynch.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User findByUsername(String username) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return findByUsername(connection, username);
        }
    }

    public User findByUsername(Connection connection, String username) throws Exception {

        String sql = """
                SELECT user_id, username, password_hash,
                       role, student_id, company_id
                FROM users
                WHERE username = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return new User(
                            result.getInt("user_id"),
                            result.getString("username"),
                            result.getString("password_hash"),
                            UserRole.fromLabel(result.getString("role")),
                            DaoUtil.getNullableInt(result, "student_id"),
                            DaoUtil.getNullableInt(result, "company_id")
                    );
                }
            }
        }

        return null;
    }

    public int createUser(
            Connection connection,
            String username,
            String passwordHash,
            UserRole role,
            Integer studentId,
            Integer companyId
    ) throws Exception {

        String sql = """
                INSERT INTO users
                (username, password_hash, role, student_id, company_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, role.getLabel());
            DaoUtil.setNullableInt(statement, 4, studentId);
            DaoUtil.setNullableInt(statement, 5, companyId);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new Exception("User account creation failed.");
    }
}
