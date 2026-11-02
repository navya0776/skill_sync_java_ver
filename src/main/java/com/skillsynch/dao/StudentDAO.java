package com.skillsynch.dao;

import com.skillsynch.config.DatabaseConfig;
import java.util.ArrayList;
import java.util.List;
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
                SELECT student_id, name, branch, year, cgpa, email, resume_url
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
                    student.put(
                            "resume_url",
                            result.getString("resume_url")
                    );
                    return student;
                }
            }
        }

        return null;
    }
    // GET ALL STUDENTS FOR ADMIN DASHBOARD
    public List<Map<String, Object>> getAllStudents(
            Connection connection
    ) throws Exception {

        String sql = """
            SELECT
                s.student_id,
                s.name,
                s.branch,
                s.year,
                s.cgpa,
                s.email,
                s.resume_url,
                u.user_id,
                u.username
            FROM Students s
            LEFT JOIN Users u
                ON s.student_id = u.student_id
                AND u.role = 'student'
            ORDER BY s.student_id
            """;

        List<Map<String, Object>> students = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Map<String, Object> student = new HashMap<>();

                student.put("student_id", result.getInt("student_id"));
                student.put("name", result.getString("name"));
                student.put("branch", result.getString("branch"));
                student.put("year", result.getInt("year"));
                student.put("cgpa", result.getDouble("cgpa"));
                student.put("email", result.getString("email"));
                student.put("resume_url", result.getString("resume_url"));
                student.put("user_id", result.getObject("user_id"));
                student.put("username", result.getString("username"));

                students.add(student);
            }
        }

        return students;
    }
    // DELETE STUDENT
    public void deleteStudent(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
            DELETE FROM Students
            WHERE student_id = ?
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new Exception("Student not found.");
            }
        }
    }
    // UPDATE STUDENT PROFILE
    public void updateStudent(
            Connection connection,
            int studentId,
            String name,
            String branch,
            int year,
            double cgpa,
            String email,
            String resumeUrl
    ) throws Exception {

        String sql = """
            UPDATE Students
            SET name = ?,
                branch = ?,
                year = ?,
                cgpa = ?,
                email = ?,
                resume_url = ?
            WHERE student_id = ?
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, branch);
            statement.setInt(3, year);
            statement.setDouble(4, cgpa);
            statement.setString(5, email);
            statement.setString(6, resumeUrl);
            statement.setInt(7, studentId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new Exception("Student profile not found.");
            }
        }
    }
}