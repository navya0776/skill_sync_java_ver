
package com.skillsynch.service;

import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.CompanyDAO;
import com.skillsynch.dao.StudentDAO;
import com.skillsynch.dao.UserDAO;
import com.skillsynch.model.UserRole;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;

public class RegistrationService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();
    private final UserDAO userDAO = new UserDAO();

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

        if (year < 1 || year > 4) {
            throw new IllegalArgumentException(
                    "Academic year must be between 1 and 4."
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
                userDAO.createUser(
                        connection,
                        email.trim(),
                        passwordHash,
                        UserRole.STUDENT,
                        studentId,
                        null
                );

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

        String normalizedIndustry = (industry == null || industry.isBlank())
                ? null : industry.trim();
        String normalizedDescription = (description == null || description.isBlank())
                ? null : description.trim();

        try (Connection connection = DatabaseConfig.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // Create company profile
                int companyId = companyDAO.createCompany(
                        connection,
                        companyName.trim(),
                        normalizedIndustry,
                        normalizedDescription
                );

                // Create login account
                userDAO.createUser(
                        connection,
                        email.trim(),
                        passwordHash,
                        UserRole.COMPANY,
                        null,
                        companyId
                );

                connection.commit();

            } catch (Exception e) {

                connection.rollback();
                throw e;
            }
        }
    }
}
