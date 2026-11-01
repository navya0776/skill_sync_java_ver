package com.skillsynch.dao;

import com.skillsynch.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class StudentDAO {

    // CREATE STUDENT DURING REGISTRATION
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

    // GET STUDENT PROFILE FROM MYSQL
    public Map<String, Object> getStudentById(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
                SELECT student_id, name, branch, year, cgpa, email
                FROM Students
                WHERE student_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    Map<String, Object> student = new HashMap<>();

                    student.put(
                            "student_id",
                            result.getInt("student_id")
                    );

                    student.put(
                            "name",
                            result.getString("name")
                    );

                    student.put(
                            "branch",
                            result.getString("branch")
                    );

                    student.put(
                            "year",
                            result.getInt("year")
                    );

                    student.put(
                            "cgpa",
                            result.getDouble("cgpa")
                    );

                    student.put(
                            "email",
                            result.getString("email")
                    );

                    return student;
                }
            }
        }

        return null;
    }
}