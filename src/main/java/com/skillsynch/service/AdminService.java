package com.skillsynch.service;

import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.ApplicationDAO;
import com.skillsynch.dao.CompanyDAO;
import com.skillsynch.dao.InternshipDAO;
import com.skillsynch.dao.SkillDAO;
import com.skillsynch.dao.StudentDAO;
import com.skillsynch.model.Application;
import com.skillsynch.model.ApplicationStats;
import com.skillsynch.model.Company;
import com.skillsynch.model.Internship;
import com.skillsynch.model.InternshipStatus;
import com.skillsynch.model.Student;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Business / data-access logic for the admin side of SkillSynch.
 * Contains no JavaFX code, so it can be reused and tested on its own.
 */
public class AdminService {

    /** Thrown when user-entered data fails validation. The message is safe to show to the user. */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private final InternshipDAO internshipDAO = new InternshipDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    // =========================================================
    // INTERNSHIP APPROVALS
    // =========================================================

    public List<Internship> getAllInternships() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return internshipDAO.getAllInternships(connection);
        }
    }

    public void updateInternshipStatus(
            int internshipId,
            InternshipStatus status
    ) throws Exception {

        if (status == null || status == InternshipStatus.PENDING) {
            throw new ValidationException("Invalid internship status.");
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            internshipDAO.updateInternshipStatus(
                    connection,
                    internshipId,
                    status
            );
        }
    }

    public void updateInternshipStatus(
            int internshipId,
            String newStatus
    ) throws Exception {

        if (newStatus == null || newStatus.isBlank()) {
            throw new ValidationException("Please select a valid internship status.");
        }

        InternshipStatus status;
        try {
            status = InternshipStatus.fromLabel(newStatus.trim());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid internship status: " + newStatus);
        }

        updateInternshipStatus(internshipId, status);
    }

    /**
     * Creates an internship on behalf of a company (status starts as Pending).
     */
    public void addInternship(
            Company company,
            String role,
            String description,
            String cgpaText,
            String positionsText,
            LocalDate deadline
    ) throws ValidationException, Exception {

        role = role == null ? "" : role.trim();
        description = description == null ? "" : description.trim();
        cgpaText = cgpaText == null ? "" : cgpaText.trim();
        positionsText = positionsText == null ? "" : positionsText.trim();

        if (company == null || role.isEmpty() || description.isEmpty()
                || cgpaText.isEmpty() || positionsText.isEmpty() || deadline == null) {
            throw new ValidationException("Please fill in all fields.");
        }

        double minCgpa;
        int positions;

        try {
            minCgpa = Double.parseDouble(cgpaText);
            positions = Integer.parseInt(positionsText);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Enter valid numbers for CGPA and positions.");
        }

        if (minCgpa < 0 || minCgpa > 10) {
            throw new ValidationException("CGPA must be between 0 and 10.");
        }

        if (positions <= 0) {
            throw new ValidationException("Number of positions must be greater than 0.");
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            internshipDAO.createInternship(
                    connection,
                    company.getCompanyId(),
                    company.getCompanyName(),
                    role,
                    description,
                    minCgpa,
                    positions,
                    deadline
            );
        }
    }

    // =========================================================
    // REPORTS
    // =========================================================

    public ApplicationStats getApplicationStatistics() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.getApplicationStatistics(connection);
        }
    }

    // =========================================================
    // SKILLS
    // =========================================================

    public void addSkill(String skillName, String category) throws ValidationException, Exception {

        skillName = skillName == null ? "" : skillName.trim();
        category = category == null ? "" : category.trim();

        if (skillName.isEmpty() || category.isEmpty()) {
            throw new ValidationException("Please fill in both fields.");
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            skillDAO.addSkill(connection, skillName, category);
        }
    }

    // =========================================================
    // COMPANIES
    // =========================================================

    public List<Company> getAllCompanies() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return companyDAO.getAllCompanies(connection);
        }
    }

    public void addCompany(String name, String industry, String description)
            throws ValidationException, Exception {

        name = name == null ? "" : name.trim();
        industry = industry == null ? "" : industry.trim();
        description = description == null ? "" : description.trim();

        if (name.isEmpty() || industry.isEmpty()) {
            throw new ValidationException("Company name and industry are required.");
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            companyDAO.createCompany(
                    connection,
                    name,
                    industry,
                    description.isEmpty() ? null : description
            );
        }
    }

    // =========================================================
    // STUDENTS
    // =========================================================

    public List<Student> getAllStudents() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return studentDAO.getAllStudents(connection);
        }
    }

    public void deleteStudent(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            studentDAO.deleteStudent(connection, studentId);
        }
    }

    /** Case-insensitive search on name (ignoring extra spaces) or email. */
    public List<Student> filterStudents(
            List<Student> students,
            String search
    ) {

        String normalizedSearch = normalizeSpaces(search);

        List<Student> result = new ArrayList<>();

        for (Student student : students) {

            String name = normalizeSpaces(student.getName());
            String email = student.getEmail() == null
                    ? ""
                    : student.getEmail().trim().toLowerCase(Locale.ROOT);

            if (name.contains(normalizedSearch) || email.contains(normalizedSearch)) {
                result.add(student);
            }
        }

        return result;
    }

    // =========================================================
    // APPLICATIONS
    // =========================================================

    public List<Application> getAllApplications() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.getAllApplications(connection);
        }
    }

    /** Case-insensitive search on student name, email or company name. */
    public List<Application> filterApplications(
            List<Application> applications,
            String search
    ) {

        String term = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);

        List<Application> result = new ArrayList<>();

        for (Application application : applications) {

            String studentName = application.getStudentName() == null ? "" : application.getStudentName();
            String email = application.getStudentEmail() == null ? "" : application.getStudentEmail();
            String company = application.getCompanyName() == null ? "" : application.getCompanyName();

            if (studentName.toLowerCase(Locale.ROOT).contains(term)
                    || email.toLowerCase(Locale.ROOT).contains(term)
                    || company.toLowerCase(Locale.ROOT).contains(term)) {
                result.add(application);
            }
        }

        return result;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String normalizeSpaces(String text) {
        return text == null
                ? ""
                : text.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}