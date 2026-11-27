package com.skillsynch.service;

import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.ApplicationDAO;
import com.skillsynch.dao.EligibilityDAO;
import com.skillsynch.dao.InternshipDAO;
import com.skillsynch.dao.SkillDAO;
import com.skillsynch.dao.StudentDAO;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentService {

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private final StudentDAO studentDAO = new StudentDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final InternshipDAO internshipDAO = new InternshipDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final EligibilityDAO eligibilityDAO = new EligibilityDAO();

    // =========================================================
    // PROFILE
    // =========================================================

    /** Returns the student's profile row, or null/empty if not found. */
    public Map<String, Object> getStudentProfile(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return studentDAO.getStudentById(connection, studentId);
        }
    }


    public void updateProfile(
            int studentId,
            String name,
            String email,
            String branch,
            String yearText,
            String cgpaText,
            String resumeUrl,
            File newResumeFile
    ) throws ValidationException, Exception {

        name = name == null ? "" : name.trim();
        email = email == null ? "" : email.trim();
        branch = branch == null ? "" : branch.trim();
        resumeUrl = resumeUrl == null ? "" : resumeUrl.trim();

        if (name.isEmpty() || email.isEmpty() || branch.isEmpty()) {
            throw new ValidationException("Please fill all required fields.");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidationException("Enter a valid email.");
        }

        int year;
        double cgpa;

        try {
            year = Integer.parseInt(yearText == null ? "" : yearText.trim());
            cgpa = Double.parseDouble(cgpaText == null ? "" : cgpaText.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException("Year and CGPA must be valid numbers.");
        }

        if (year < 1 || year > 5) {
            throw new ValidationException("Year must be between 1 and 5.");
        }

        if (cgpa < 0 || cgpa > 10) {
            throw new ValidationException("CGPA must be between 0 and 10.");
        }

        // Only copy the file once everything else is valid
        if (newResumeFile != null) {
            resumeUrl = storeResume(studentId, newResumeFile);
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            studentDAO.updateStudent(
                    connection,
                    studentId,
                    name,
                    branch,
                    year,
                    cgpa,
                    email,
                    resumeUrl
            );
        }
    }

    /** Copies the chosen resume into ./uploads/student_{id}_resume.pdf and returns its absolute path. */
    public String storeResume(int studentId, File source) throws Exception {

        Path uploadDirectory = Path.of("uploads").toAbsolutePath();
        Files.createDirectories(uploadDirectory);

        Path destination = uploadDirectory.resolve("student_" + studentId + "_resume.pdf");

        Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        return destination.toString();
    }

    /**
     * Decides which resume path to use: the newly chosen file if there is one,
     * otherwise the one stored in the database. Returns "" if neither exists.
     */
    public String resolveResumePath(File selectedResume, Object storedResume) {
        if (selectedResume != null) {
            return selectedResume.getAbsolutePath();
        }
        return storedResume == null ? "" : storedResume.toString();
    }

    /** Copies a resume file to a location the student picked. */
    public void copyResume(File source, File destination) throws Exception {
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // =========================================================
    // SKILLS
    // =========================================================

    public List<Map<String, Object>> getAllSkills() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return skillDAO.getAllSkills(connection);
        }
    }

    public List<Map<String, Object>> getStudentSkills(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return skillDAO.getStudentSkills(connection, studentId);
        }
    }

    public void addStudentSkill(int studentId, int skillId, int proficiency) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            skillDAO.addStudentSkill(connection, studentId, skillId, proficiency);
        }
    }

    public void removeStudentSkill(int studentId, int skillId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            skillDAO.removeStudentSkill(connection, studentId, skillId);
        }
    }

    // =========================================================
    // INTERNSHIPS & APPLICATIONS
    // =========================================================

    /** Only internships that are Approved or Published are visible to students. */
    public List<Map<String, Object>> getAvailableInternships() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return filterAvailable(internshipDAO.getAllInternships(connection));
        }
    }

    public boolean hasApplied(int studentId, int internshipId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.hasApplied(connection, studentId, internshipId);
        }
    }

    /** Returns true if the application was submitted, false if the student had already applied. */
    public boolean applyForInternship(int studentId, int internshipId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.applyForInternship(connection, studentId, internshipId);
        }
    }

    public List<Map<String, Object>> getStudentApplications(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.getStudentApplications(connection, studentId);
        }
    }

    // =========================================================
    // ELIGIBILITY
    // =========================================================

    /**
     * Re-calculates eligibility for every available internship,
     * saves it, then returns the student's eligibility results.
     */
    public List<Map<String, Object>> evaluateAndGetEligibility(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {

            List<Map<String, Object>> available =
                    filterAvailable(internshipDAO.getAllInternships(connection));

            for (Map<String, Object> internship : available) {
                int internId = Integer.parseInt(String.valueOf(internship.get("intern_id")));
                eligibilityDAO.evaluateAndSave(connection, studentId, internId);
            }

            return eligibilityDAO.getStudentEligibility(connection, studentId);
        }
    }

    // =========================================================
    // OFFERS
    // =========================================================

    public List<Map<String, Object>> getStudentOffers(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.getStudentOffers(connection, studentId);
        }
    }

    public void updateOfferStatus(int offerId, int studentId, String status) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            applicationDAO.updateOfferStatus(connection, offerId, studentId, status);
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private List<Map<String, Object>> filterAvailable(List<Map<String, Object>> internships) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> internship : internships) {
            String status = String.valueOf(internship.get("status"));

            if (status.equals("Approved") || status.equals("Published")) {
                result.add(internship);
            }
        }

        return result;
    }
}