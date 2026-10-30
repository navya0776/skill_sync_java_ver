
package com.skillsynch.service;

import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.StudentDAO;
import com.skillsynch.dao.CompanyDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistrationService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    // STUDENT REGISTRATION
    public void registerStudent(
            String name,
            String email,
            String password,
            String branch,
            int year,
            double cgpa
    ) throws Exception {

        // Validate required fields
        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || branch == null || branch.isBlank()) {

            throw new IllegalArgumentException(
                    "Please fill in all required student details."
            );
        }

        if (year < 1 || year > 6) {
            throw new IllegalArgumentException(
                    "Invalid academic year."
            );
        }

        if (cgpa < 0 || cgpa > 10) {
            throw new IllegalArgumentException(
                    "CGPA must be between 0 and 10."
            );
        }

        // Hash password
        String passwordHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );

        try (Connection connection = DatabaseConfig.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // Create student profile
                int studentId = studentDAO.createStudent(
                        connection,
                        name.trim(),
                        branch.trim(),
                        year,
                        cgpa,
                        email.trim()
                );

                // Create login account
                String sql = """
                        INSERT INTO Users
                        (username, password_hash, role, student_id)
                        VALUES (?, ?, 'student', ?)
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, email.trim());
                    statement.setString(2, passwordHash);
                    statement.setInt(3, studentId);

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {

                connection.rollback();
                throw e;
            }
        }
    }


    // COMPANY REGISTRATION
    public void registerCompany(
            String companyName,
            String email,
            String password,
            String industry,
            String description
    ) throws Exception {

        // Validate required fields
        if (companyName == null || companyName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {

            throw new IllegalArgumentException(
                    "Company name, email and password are required."
            );
        }

        // Hash password
        String passwordHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );

        try (Connection connection = DatabaseConfig.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // Create company profile
                int companyId = companyDAO.createCompany(
                        connection,
                        companyName.trim(),
                        industry == null ? null : industry.trim(),
                        description == null ? null : description.trim()
                );

                // Create login account
                String sql = """
                        INSERT INTO Users
                        (username, password_hash, role, company_id)
                        VALUES (?, ?, 'company', ?)
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, email.trim());
                    statement.setString(2, passwordHash);
                    statement.setInt(3, companyId);

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {

                connection.rollback();
                throw e;
            }
        }
    }
}
