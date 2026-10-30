
package com.skillsynch.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDAO {

    public int createStudent(
            Connection connection,
            String name,
            String branch,
            int year,
            double cgpa,
            String email
    ) throws Exception {

        String sql = """
                INSERT INTO Students
                (name, branch, year, cgpa, email)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             PreparedStatement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(1, name);
            statement.setString(2, branch);
            statement.setInt(3, year);
            statement.setDouble(4, cgpa);
            statement.setString(5, email);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new Exception("Student creation failed.");
    }
}
