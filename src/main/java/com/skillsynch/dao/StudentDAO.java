package com.skillsynch.dao;

import com.skillsynch.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // CREATE STUDENT DURING REGISTRATION
    // (signature unchanged, so existing registration code keeps working)
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

    // Convenience overload that takes a Student object
    public int createStudent(Connection connection, Student student) throws Exception {
        return createStudent(
                connection,
                student.getName(),
                student.getBranch(),
                student.getYear(),
                student.getCgpa(),
                student.getEmail()
        );
    }

    // GET STUDENT PROFILE (returns null if not found)
    public Student getStudentById(
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
                    return mapStudent(result);
                }
            }
        }

        return null;
    }

    // GET ALL STUDENTS FOR ADMIN DASHBOARD
    public List<Student> getAllStudents(
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

        List<Student> students = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Student student = mapStudent(result);

                int userId = result.getInt("user_id");

                if (!result.wasNull()) {
                    student.setUserId(userId);
                }

                student.setUsername(result.getString("username"));

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

    // UPDATE STUDENT PROFILE (uses student.getStudentId() to find the row)
    public void updateStudent(
            Connection connection,
            Student student
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

            statement.setString(1, student.getName());
            statement.setString(2, student.getBranch());
            statement.setInt(3, student.getYear());
            statement.setDouble(4, student.getCgpa());
            statement.setString(5, student.getEmail());
            statement.setString(6, student.getResumeUrl());
            statement.setInt(7, student.getStudentId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new Exception("Student profile not found.");
            }
        }
    }

    // Turns the current ResultSet row into a Student
    private Student mapStudent(ResultSet result) throws SQLException {

        return new Student(
                result.getInt("student_id"),
                result.getString("name"),
                result.getString("branch"),
                result.getInt("year"),
                result.getDouble("cgpa"),
                result.getString("email"),
                result.getString("resume_url")
        );
    }
}