package com.skillsynch.service;

import com.skillsynch.config.DatabaseConfig;
import com.skillsynch.dao.ApplicationDAO;
import com.skillsynch.dao.CompanyDAO;
import com.skillsynch.dao.InternshipDAO;
import com.skillsynch.dao.SkillDAO;
import com.skillsynch.model.Application;
import com.skillsynch.model.ApplicationStatus;
import com.skillsynch.model.Company;
import com.skillsynch.model.Internship;
import com.skillsynch.model.Offer;
import com.skillsynch.model.Skill;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Business / data-access logic for the company side of SkillSynch.
 * Contains no JavaFX code, so it can be reused and tested on its own.
 */
public class CompanyService {

    /** Thrown when user-entered data fails validation. The message is safe to show to the user. */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private final SkillDAO skillDAO = new SkillDAO();
    private final InternshipDAO internshipDAO = new InternshipDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    // =========================================================
    // SKILLS
    // =========================================================

    public List<Skill> getAllSkills() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return skillDAO.getAllSkills(connection);
        }
    }

    // =========================================================
    // INTERNSHIPS
    // =========================================================

    /**
     * Validates and posts a new internship together with its required skills.
     * The internship and its skills are saved in one transaction.
     *
     * @param requiredSkills map of skill id to minimum proficiency level
     * @return the new internship's id
     */
    public int postInternship(
            int companyId,
            String role,
            String description,
            String cgpaText,
            String positionsText,
            LocalDate deadline,
            Map<Integer, Integer> requiredSkills
    ) throws ValidationException, Exception {

        if (isBlank(role) || isBlank(description) || isBlank(cgpaText)
                || isBlank(positionsText) || deadline == null) {
            throw new ValidationException("Please fill all internship fields.");
        }

        double cgpa;
        int positions;

        try {
            cgpa = Double.parseDouble(cgpaText.trim());
            positions = Integer.parseInt(positionsText.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException(
                    "CGPA must be a number and positions must be an integer."
            );
        }

        if (cgpa < 0 || cgpa > 10 || positions <= 0) {
            throw new ValidationException("Enter a valid CGPA and number of positions.");
        }

        if (deadline.isBefore(LocalDate.now())) {
            throw new ValidationException("Deadline cannot be in the past.");
        }

        if (requiredSkills == null || requiredSkills.isEmpty()) {
            throw new ValidationException("Please select at least one required skill.");
        }

        try (Connection connection = DatabaseConfig.getConnection()) {

            connection.setAutoCommit(false);

            try {
                Company company = companyDAO.getCompanyById(connection, companyId);
                if (company == null) {
                    throw new ValidationException("Company profile not found.");
                }

                int internshipId = internshipDAO.createInternship(
                        connection,
                        companyId,
                        company.getCompanyName(),
                        role.trim(),
                        description.trim(),
                        cgpa,
                        positions,
                        deadline
                );

                internshipDAO.addRequiredSkills(connection, internshipId, requiredSkills);

                connection.commit();

                return internshipId;

            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public List<Internship> getCompanyInternships(int companyId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return internshipDAO.getCompanyInternships(connection, companyId);
        }
    }

    // =========================================================
    // APPLICATIONS
    // =========================================================

    public List<Application> getCompanyApplications(int companyId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.getCompanyApplications(connection, companyId);
        }
    }

    public void updateApplicationStatus(
            int applicationId,
            int companyId,
            ApplicationStatus newStatus
    ) throws ValidationException, Exception {

        if (newStatus == null) {
            throw new ValidationException("Please select a status.");
        }

        if (!List.of(
                ApplicationStatus.SHORTLISTED,
                ApplicationStatus.INTERVIEW,
                ApplicationStatus.SELECTED,
                ApplicationStatus.REJECTED
        ).contains(newStatus)) {
            throw new ValidationException("Invalid application status: " + newStatus);
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            applicationDAO.updateApplicationStatus(connection, applicationId, companyId, newStatus);
        }
    }

    public void updateApplicationStatus(int applicationId, int companyId, String newStatus)
            throws ValidationException, Exception {

        if (isBlank(newStatus)) {
            throw new ValidationException("Please select a status.");
        }

        ApplicationStatus status;
        try {
            status = ApplicationStatus.fromLabel(newStatus.trim());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid application status: " + newStatus);
        }

        updateApplicationStatus(applicationId, companyId, status);
    }

    public List<Application> getShortlistedStudents(int companyId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.getShortlistedStudents(connection, companyId);
        }
    }

    /** Applications whose status is "Selected", i.e. students who can receive an offer. */
    public List<Application> getSelectedApplications(int companyId) throws Exception {

        List<Application> selected = new ArrayList<>();

        for (Application application : getCompanyApplications(companyId)) {
            if (application.getStatus() == ApplicationStatus.SELECTED) {
                selected.add(application);
            }
        }

        return selected;
    }

    // =========================================================
    // COMPANY PROFILE
    // =========================================================

    /** Returns the company's profile row, or null if it does not exist. */
    public Company getCompany(int companyId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return companyDAO.getCompanyById(connection, companyId);
        }
    }

    /** Returns true if the profile was updated. */
    public boolean updateCompanyProfile(
            int companyId,
            String name,
            String industry,
            String description
    ) throws ValidationException, Exception {

        name = name == null ? "" : name.trim();
        industry = industry == null ? "" : industry.trim();
        description = description == null ? "" : description.trim();

        if (name.isEmpty() || industry.isEmpty()) {
            throw new ValidationException("Company name and industry are required.");
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            return companyDAO.updateCompany(
                    connection,
                    companyId,
                    name,
                    industry,
                    description.isEmpty() ? null : description
            );
        }
    }

    // =========================================================
    // OFFERS
    // =========================================================

    /** Returns true if the offer was created. */
    public boolean createOffer(int companyId, Integer applicationId, LocalDate joiningDate)
            throws ValidationException, Exception {

        if (applicationId == null || joiningDate == null) {
            throw new ValidationException("Please select a student and joining date.");
        }

        if (!joiningDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Joining date must be in the future.");
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                boolean created = applicationDAO.createOffer(
                        connection,
                        applicationId,
                        companyId,
                        joiningDate
                );
                connection.commit();
                return created;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public List<Offer> getCompanyOffers(int companyId) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return applicationDAO.getCompanyOffers(connection, companyId);
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}