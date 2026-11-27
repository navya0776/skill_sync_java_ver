package com.skillsynch.service;

import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.ApplicationDAO;
import com.skillsynch.dao.EligibilityDAO;
import com.skillsynch.dao.InternshipDAO;
import com.skillsynch.dao.SkillDAO;
import com.skillsynch.dao.StudentDAO;
import com.skillsynch.model.Application;
import com.skillsynch.model.Eligibility;
import com.skillsynch.model.Internship;
import com.skillsynch.model.InternshipStatus;
import com.skillsynch.model.Offer;
import com.skillsynch.model.OfferStatus;
import com.skillsynch.model.Skill;
import com.skillsynch.model.Student;
import com.skillsynch.model.StudentSkill;
import com.skillsynch.model.EligibilityStatus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    /** Returns the student's profile, or null if not found. */
    public Student getStudentProfile(int studentId) throws Exception {
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

        if (year < 1 || year > 4) {
            throw new ValidationException("Year must be between 1 and 4.");
        }

        if (cgpa < 0 || cgpa > 10) {
            throw new ValidationException("CGPA must be between 0 and 10.");
        }

        // Only copy the file once everything else is valid
        if (newResumeFile != null) {
            resumeUrl = storeResume(studentId, newResumeFile);
        }

        String storedResumeUrl = resumeUrl.isEmpty() ? null : resumeUrl;
        Student student = new Student(studentId, name, branch, year, cgpa, email, storedResumeUrl);

        try (Connection connection = DatabaseConfig.getConnection()) {
            studentDAO.updateStudent(connection, student);
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
    public String resolveResumePath(File selectedResume, String storedResume) {
        if (selectedResume != null) {
            return selectedResume.getAbsolutePath();
        }
        return storedResume == null ? "" : storedResume;
    }

    /** Copies a resume file to a location the student picked. */
    public void copyResume(File source, File destination) throws Exception {
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // =========================================================
    // SKILLS
    // =========================================================

    public List<Skill> getAllSkills() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return skillDAO.getAllSkills(connection);
        }
    }

    public List<StudentSkill> getStudentSkills(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return skillDAO.getStudentSkills(connection, studentId);
        }
    }

    public void addStudentSkill(int studentId, int skillId, int proficiency) throws Exception {
        if (proficiency < 1 || proficiency > 5) {
            throw new ValidationException("Proficiency level must be between 1 and 5.");
        }

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
    public List<Internship> getAvailableInternships() throws Exception {
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

    public List<Application> getStudentApplications(int studentId) throws Exception {
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
    public List<Eligibility> evaluateAndGetEligibility(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {

            List<Internship> available =
                    filterAvailable(internshipDAO.getAllInternships(connection));

            for (Internship internship : available) {
                eligibilityDAO.evaluateAndSave(connection, studentId, internship.getInternId());
            }

            return eligibilityDAO.getStudentEligibility(connection, studentId);
        }
    }

    public Map<Integer, Eligibility> getEligibilityByInternship(int studentId) throws Exception {
        Map<Integer, Eligibility> map = new HashMap<>();
        for (Eligibility e : evaluateAndGetEligibility(studentId)) {
            map.put(e.getInternId(), e);
        }
        return map;
    }

    public static boolean isEligibleStatus(Eligibility e) {
        return e != null && e.getEligibilityStatus() == EligibilityStatus.ELIGIBLE;
    }

    /** Everything the student still needs: CGPA shortfall plus missing/low skills. */
    public List<String> getEligibilityGaps(int studentId, Internship internship) throws Exception {
        List<String> gaps = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection()) {
            Student student = studentDAO.getStudentById(connection, studentId);

            if (internship.getMinCgpa() != null) {
                double min = internship.getMinCgpa().doubleValue();
                Double mine = student == null ? null : student.getCgpa();
                if (mine == null || mine < min) {
                    gaps.add("CGPA of at least " + min + " (yours: " + (mine == null ? "not set" : mine) + ")");
                }
            }

            gaps.addAll(skillDAO.getMissingSkills(connection, studentId, internship.getInternId()));
        }
        return gaps;
    }

    // Replace the existing applyForInternship
    public boolean applyForInternship(int studentId, int internshipId) throws Exception {
        Eligibility e = getEligibilityByInternship(studentId).get(internshipId);
        if (!isEligibleStatus(e)) {
            throw new ValidationException("You are not eligible for this internship.");
        }
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.applyForInternship(connection, studentId, internshipId);
        }
    }

    // =========================================================
    // OFFERS
    // =========================================================

    public List<Offer> getStudentOffers(int studentId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.getStudentOffers(connection, studentId);
        }
    }

    public void updateOfferStatus(int offerId, int studentId, OfferStatus status) throws Exception {
        if (status != OfferStatus.ACCEPTED && status != OfferStatus.REJECTED) {
            throw new ValidationException("Invalid offer status.");
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                applicationDAO.updateOfferStatus(connection, offerId, studentId, status);
                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public void updateOfferStatus(int offerId, int studentId, String status) throws Exception {
        if (status == null || status.isBlank()) {
            throw new ValidationException("Please select an offer status.");
        }

        OfferStatus parsedStatus;
        try {
            parsedStatus = OfferStatus.fromLabel(status.trim());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid offer status: " + status);
        }

        updateOfferStatus(offerId, studentId, parsedStatus);
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private List<Internship> filterAvailable(List<Internship> internships) {
        List<Internship> result = new ArrayList<>();

        for (Internship internship : internships) {
            InternshipStatus status = internship.getStatus();

            if (status == InternshipStatus.APPROVED || status == InternshipStatus.PUBLISHED) {
                result.add(internship);
            }
        }

        return result;
    }
}