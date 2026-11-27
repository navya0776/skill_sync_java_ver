package com.skillsynch.dao;

import com.skillsynch.model.Application;
import com.skillsynch.model.ApplicationStats;
import com.skillsynch.model.ApplicationStatus;
import com.skillsynch.model.Offer;
import com.skillsynch.model.OfferStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public boolean applyForInternship(
            Connection connection,
            int studentId,
            int internId
    ) throws Exception {

        String checkSql = """
            SELECT application_id
            FROM applications
            WHERE student_id = ? AND intern_id = ?
            """;

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setInt(1, studentId);
            check.setInt(2, internId);

            if (check.executeQuery().next()) {
                return false;
            }
        }

        String sql = """
            INSERT INTO applications (student_id, intern_id, status)
            VALUES (?, ?, 'Applied')
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);
            ps.executeUpdate();
        }

        return true;
    }

    public boolean hasApplied(
            Connection connection,
            int studentId,
            int internId
    ) throws Exception {

        String sql = """
            SELECT application_id
            FROM applications
            WHERE student_id = ? AND intern_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);

            return ps.executeQuery().next();
        }
    }

    // STUDENT: MY APPLICATIONS (company name and role are filled in)
    public List<Application> getStudentApplications(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
            SELECT
                a.application_id,
                a.student_id,
                a.intern_id,
                a.status,
                a.application_date,
                i.company_name,
                i.role
            FROM applications a
            JOIN internships i ON a.intern_id = i.intern_id
            WHERE a.student_id = ?
            ORDER BY a.application_date DESC
            """;

        List<Application> applications = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application application = mapApplication(rs);

                    application.setCompanyName(rs.getString("company_name"));
                    application.setRole(rs.getString("role"));

                    applications.add(application);
                }
            }
        }

        return applications;
    }

    // COMPANY: APPLICATIONS TO THIS COMPANY'S INTERNSHIPS
    public List<Application> getCompanyApplications(
            Connection connection,
            int companyId
    ) throws Exception {

        String sql = """
            SELECT
                a.application_id,
                a.student_id,
                a.intern_id,
                a.status,
                a.application_date,
                s.name AS student_name,
                s.email,
                s.branch,
                s.cgpa,
                i.role
            FROM applications a
            JOIN students s ON a.student_id = s.student_id
            JOIN internships i ON a.intern_id = i.intern_id
            WHERE i.company_id = ?
            ORDER BY a.application_date DESC
            """;

        List<Application> applications = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Application application = mapApplication(rs);
                    fillStudentDetails(application, rs);
                    application.setRole(rs.getString("role"));

                    applications.add(application);
                }
            }
        }

        return applications;
    }

    public void updateApplicationStatus(
            Connection connection,
            int applicationId,
            int companyId,
            ApplicationStatus status
    ) throws Exception {

        if (status == null || !List.of(
                ApplicationStatus.SHORTLISTED,
                ApplicationStatus.INTERVIEW,
                ApplicationStatus.SELECTED,
                ApplicationStatus.REJECTED
        ).contains(status)) {
            throw new IllegalArgumentException("Invalid application status.");
        }

        String sql = """
            UPDATE applications a
            JOIN internships i ON a.intern_id = i.intern_id
            SET a.status = ?
            WHERE a.application_id = ?
            AND i.company_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.getLabel());
            ps.setInt(2, applicationId);
            ps.setInt(3, companyId);

            if (ps.executeUpdate() == 0) {
                throw new Exception("Application not found for this company.");
            }
        }
    }

    // COMPANY: SHORTLISTED / INTERVIEW / SELECTED STUDENTS
    public List<Application> getShortlistedStudents(
            Connection connection,
            int companyId
    ) throws Exception {

        String sql = """
            SELECT
                a.application_id,
                a.student_id,
                a.intern_id,
                a.status,
                a.application_date,
                s.name AS student_name,
                s.email,
                s.branch,
                s.cgpa,
                i.role
            FROM applications a
            JOIN students s ON a.student_id = s.student_id
            JOIN internships i ON a.intern_id = i.intern_id
            WHERE i.company_id = ?
            AND a.status IN ('Shortlisted', 'Interview', 'Selected')
            ORDER BY a.application_date DESC
            """;

        List<Application> students = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Application application = mapApplication(rs);
                    fillStudentDetails(application, rs);
                    application.setRole(rs.getString("role"));

                    students.add(application);
                }
            }
        }

        return students;
    }

    // ADMIN: ALL APPLICATIONS
    public List<Application> getAllApplications(
            Connection connection
    ) throws Exception {

        String sql = """
            SELECT
                a.application_id,
                a.student_id,
                a.intern_id,
                a.status,
                a.application_date,
                s.name AS student_name,
                s.email,
                s.branch,
                s.cgpa,
                i.company_name,
                i.role
            FROM applications a
            JOIN students s ON a.student_id = s.student_id
            JOIN internships i ON a.intern_id = i.intern_id
            ORDER BY a.application_date DESC
            """;

        List<Application> applications = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Application application = mapApplication(rs);
                fillStudentDetails(application, rs);
                application.setCompanyName(rs.getString("company_name"));
                application.setRole(rs.getString("role"));

                applications.add(application);
            }
        }

        return applications;
    }

    public ApplicationStats getApplicationStatistics(
            Connection connection
    ) throws Exception {

        String sql = """
            SELECT
                COUNT(*) AS total_applications,
                SUM(CASE WHEN status = 'Selected' THEN 1 ELSE 0 END) AS selected,
                SUM(CASE WHEN status = 'Shortlisted' THEN 1 ELSE 0 END) AS shortlisted,
                SUM(CASE WHEN status = 'Rejected' THEN 1 ELSE 0 END) AS rejected,
                SUM(CASE WHEN status = 'Offer Extended' THEN 1 ELSE 0 END) AS offers_extended
            FROM applications
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new ApplicationStats(
                        rs.getInt("total_applications"),
                        rs.getInt("selected"),
                        rs.getInt("shortlisted"),
                        rs.getInt("rejected"),
                        rs.getInt("offers_extended")
                );
            }
        }

        return new ApplicationStats(0, 0, 0, 0, 0);
    }

    public boolean createOffer(
            Connection connection,
            int applicationId,
            int companyId,
            java.sql.Date joiningDate
    ) throws Exception {

        String checkSql = """
            SELECT a.application_id
            FROM applications a
            JOIN internships i ON a.intern_id = i.intern_id
            WHERE a.application_id = ?
            AND i.company_id = ?
            AND a.status = 'Selected'
            """;

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {

            check.setInt(1, applicationId);
            check.setInt(2, companyId);

            if (!check.executeQuery().next()) {
                throw new Exception(
                        "Only selected applications can receive offers."
                );
            }
        }

        String insertSql = """
            INSERT INTO offers (
                application_id,
                joining_date,
                status
            )
            VALUES (?, ?, 'Pending')
            """;

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {

            ps.setInt(1, applicationId);
            ps.setDate(2, joiningDate);

            return ps.executeUpdate() > 0;
        }
    }

    // COMPANY: OFFERS (student name, email and role are filled in)
    public List<Offer> getCompanyOffers(
            Connection connection,
            int companyId
    ) throws Exception {

        String sql = """
            SELECT
                o.offer_id,
                o.application_id,
                o.offer_date,
                o.joining_date,
                o.status,
                s.name AS student_name,
                s.email,
                i.role
            FROM offers o
            JOIN applications a ON o.application_id = a.application_id
            JOIN students s ON a.student_id = s.student_id
            JOIN internships i ON a.intern_id = i.intern_id
            WHERE i.company_id = ?
            ORDER BY o.offer_date DESC
            """;

        List<Offer> offers = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Offer offer = mapOffer(rs);

                    offer.setStudentName(rs.getString("student_name"));
                    offer.setStudentEmail(rs.getString("email"));
                    offer.setRole(rs.getString("role"));

                    offers.add(offer);
                }
            }
        }

        return offers;
    }

    // STUDENT: OFFERS (company name and role are filled in)
    public List<Offer> getStudentOffers(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
            SELECT
                o.offer_id,
                o.application_id,
                o.offer_date,
                o.joining_date,
                o.status,
                i.company_name,
                i.role
            FROM offers o
            JOIN applications a ON o.application_id = a.application_id
            JOIN internships i ON a.intern_id = i.intern_id
            WHERE a.student_id = ?
            ORDER BY o.offer_date DESC
            """;

        List<Offer> offers = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Offer offer = mapOffer(rs);

                    offer.setCompanyName(rs.getString("company_name"));
                    offer.setRole(rs.getString("role"));

                    offers.add(offer);
                }
            }
        }

        return offers;
    }

    public void updateOfferStatus(
            Connection connection,
            int offerId,
            int studentId,
            OfferStatus status
    ) throws Exception {

        if (status != OfferStatus.ACCEPTED && status != OfferStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid offer status.");
        }

        String sql = """
            UPDATE offers o
            JOIN applications a
                ON o.application_id = a.application_id
            SET o.status = ?
            WHERE o.offer_id = ?
            AND a.student_id = ?
            AND o.status = 'Pending'
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.getLabel());
            ps.setInt(2, offerId);
            ps.setInt(3, studentId);

            if (ps.executeUpdate() == 0) {
                throw new Exception(
                        "Offer not found or it has already been processed."
                );
            }
        }
    }

    // ---------------------------------------------------------
    // Row mappers
    // ---------------------------------------------------------

    // Reads the columns that belong to the applications table itself
    private Application mapApplication(ResultSet rs) throws SQLException {
        return new Application(
                rs.getInt("application_id"),
                rs.getInt("student_id"),
                rs.getInt("intern_id"),
                ApplicationStatus.fromLabel(rs.getString("status")),
                DaoUtil.toLocalDateTime(rs.getTimestamp("application_date"))
        );
    }

    // Reads the student columns from a JOIN with the students table
    private void fillStudentDetails(Application application, ResultSet rs) throws SQLException {
        application.setStudentName(rs.getString("student_name"));
        application.setStudentEmail(rs.getString("email"));
        application.setBranch(rs.getString("branch"));
        application.setCgpa(DaoUtil.getNullableDouble(rs, "cgpa"));
    }

    // Reads the columns that belong to the offers table itself
    private Offer mapOffer(ResultSet rs) throws SQLException {
        return new Offer(
                rs.getInt("offer_id"),
                rs.getInt("application_id"),
                DaoUtil.toLocalDateTime(rs.getTimestamp("offer_date")),
                DaoUtil.toLocalDate(rs.getDate("joining_date")),
                OfferStatus.fromLabel(rs.getString("status"))
        );
    }
}