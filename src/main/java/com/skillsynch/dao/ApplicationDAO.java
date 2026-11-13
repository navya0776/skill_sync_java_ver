
package com.skillsynch.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }
        }

        String sql = """
            INSERT INTO applications (student_id, intern_id, status)
            VALUES (?, ?, 'Applied')
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);
            return ps.executeUpdate() > 0;
        }
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

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    public List<Map<String, Object>> getStudentApplications(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
        SELECT
            a.application_id,
            a.intern_id,
            a.status,
            a.application_date,
            c.company_name AS company_name,
            i.role
        FROM applications a
        JOIN internships i
            ON a.intern_id = i.intern_id
        LEFT JOIN companies c
            ON i.company_id = c.company_id
        WHERE a.student_id = ?
        ORDER BY a.application_date DESC
        """;

        List<Map<String, Object>> applications = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("application_id", rs.getInt("application_id"));
                    row.put("intern_id", rs.getInt("intern_id"));
                    row.put("status", rs.getString("status"));
                    row.put("application_date", rs.getTimestamp("application_date"));
                    row.put("company_name", rs.getString("company_name"));
                    row.put("role", rs.getString("role"));

                    applications.add(row);
                }
            }
        }

        return applications;
    }

    public List<Map<String, Object>> getCompanyApplications(
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

        List<Map<String, Object>> applications = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("application_id", rs.getInt("application_id"));
                    row.put("student_id", rs.getInt("student_id"));
                    row.put("intern_id", rs.getInt("intern_id"));
                    row.put("status", rs.getString("status"));
                    row.put("application_date", rs.getTimestamp("application_date"));
                    row.put("student_name", rs.getString("student_name"));
                    row.put("email", rs.getString("email"));
                    row.put("branch", rs.getString("branch"));
                    row.put("cgpa", rs.getDouble("cgpa"));
                    row.put("role", rs.getString("role"));

                    applications.add(row);
                }
            }
        }

        return applications;
    }

    public void updateApplicationStatus(
            Connection connection,
            int applicationId,
            int companyId,
            String status
    ) throws Exception {

        if (!List.of(
                "Shortlisted",
                "Interview",
                "Selected",
                "Rejected"
        ).contains(status)) {
            throw new IllegalArgumentException("Invalid application status.");
        }

        String sql = """
            UPDATE applications a
            JOIN internships i ON a.intern_id = i.intern_id
            SET a.status = ?
            WHERE a.application_id = ?
              AND i.company_id = ?
              AND a.status NOT IN (
                  'Offer Extended',
                  'Offer Accepted',
                  'Offer Rejected'
              )
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            ps.setInt(3, companyId);

            if (ps.executeUpdate() == 0) {
                throw new Exception(
                        "Application not found, already processed, or not owned by this company."
                );
            }
        }
    }

    public List<Map<String, Object>> getShortlistedStudents(
            Connection connection,
            int companyId
    ) throws Exception {

        String sql = """
            SELECT
                a.application_id,
                a.student_id,
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

        List<Map<String, Object>> students = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("application_id", rs.getInt("application_id"));
                    row.put("student_id", rs.getInt("student_id"));
                    row.put("status", rs.getString("status"));
                    row.put("application_date", rs.getTimestamp("application_date"));
                    row.put("student_name", rs.getString("student_name"));
                    row.put("email", rs.getString("email"));
                    row.put("branch", rs.getString("branch"));
                    row.put("cgpa", rs.getDouble("cgpa"));
                    row.put("role", rs.getString("role"));

                    students.add(row);
                }
            }
        }

        return students;
    }


    public List<Map<String, Object>> getAllApplications(
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
            c.company_name AS company_name,
            i.role
        FROM applications a
        JOIN students s
            ON a.student_id = s.student_id
        JOIN internships i
            ON a.intern_id = i.intern_id
        LEFT JOIN companies c
            ON i.company_id = c.company_id
        ORDER BY a.application_date DESC
        """;

        List<Map<String, Object>> applications = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                row.put("application_id", rs.getInt("application_id"));
                row.put("student_id", rs.getInt("student_id"));
                row.put("intern_id", rs.getInt("intern_id"));
                row.put("status", rs.getString("status"));
                row.put("application_date", rs.getTimestamp("application_date"));
                row.put("student_name", rs.getString("student_name"));
                row.put("email", rs.getString("email"));
                row.put("branch", rs.getString("branch"));
                row.put("cgpa", rs.getDouble("cgpa"));
                row.put("company_name", rs.getString("company_name"));
                row.put("role", rs.getString("role"));

                applications.add(row);
            }
        }

        return applications;
    }



    public Map<String, Integer> getApplicationStatistics(
            Connection connection
    ) throws Exception {

        String sql = """
        SELECT
            COUNT(*) AS total_applications,

            COALESCE(SUM(CASE
                WHEN status IN ('Selected', 'Offer Extended',
                                'Offer Accepted')
                THEN 1 ELSE 0
            END), 0) AS selected,

            COALESCE(SUM(CASE
                WHEN status = 'Shortlisted'
                THEN 1 ELSE 0
            END), 0) AS shortlisted,

            COALESCE(SUM(CASE
                WHEN status IN ('Rejected', 'Offer Rejected')
                THEN 1 ELSE 0
            END), 0) AS rejected,

            (SELECT COUNT(*)
             FROM offers) AS offers_extended,

            (SELECT COUNT(*)
             FROM offers
             WHERE status = 'Accepted') AS offers_accepted

        FROM applications
        """;

        Map<String, Integer> statistics = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                statistics.put("total_applications",
                        rs.getInt("total_applications"));

                statistics.put("selected",
                        rs.getInt("selected"));

                statistics.put("shortlisted",
                        rs.getInt("shortlisted"));

                statistics.put("rejected",
                        rs.getInt("rejected"));

                statistics.put("offers_extended",
                        rs.getInt("offers_extended"));

                statistics.put("offers_accepted",
                        rs.getInt("offers_accepted"));
            }
        }

        return statistics;
    }



    public boolean createOffer(
            Connection connection,
            int applicationId,
            int companyId,
            java.sql.Date joiningDate
    ) throws Exception {

        if (joiningDate == null) {
            throw new IllegalArgumentException("Joining date is required.");
        }

        String checkSql = """
            SELECT a.application_id
            FROM applications a
            JOIN internships i ON a.intern_id = i.intern_id
            WHERE a.application_id = ?
              AND i.company_id = ?
              AND a.status = 'Selected'
              AND NOT EXISTS (
                  SELECT 1 FROM offers o
                  WHERE o.application_id = a.application_id
              )
            """;

        boolean originalAutoCommit = connection.getAutoCommit();
        Savepoint savepoint = null;

        try {
            if (originalAutoCommit) {
                connection.setAutoCommit(false);
            } else {
                savepoint = connection.setSavepoint();
            }

            try (PreparedStatement check =
                         connection.prepareStatement(checkSql)) {

                check.setInt(1, applicationId);
                check.setInt(2, companyId);

                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception(
                                "This application is not eligible for a new offer."
                        );
                    }
                }
            }

            String insertSql = """
                INSERT INTO offers
                    (application_id, joining_date, status)
                VALUES (?, ?, 'Pending')
                """;

            try (PreparedStatement ps =
                         connection.prepareStatement(insertSql)) {
                ps.setInt(1, applicationId);
                ps.setDate(2, joiningDate);
                ps.executeUpdate();
            }

            String updateSql = """
                UPDATE applications
                SET status = 'Offer Extended'
                WHERE application_id = ?
                  AND status = 'Selected'
                """;

            try (PreparedStatement ps =
                         connection.prepareStatement(updateSql)) {
                ps.setInt(1, applicationId);

                if (ps.executeUpdate() == 0) {
                    throw new Exception(
                            "Could not update application status."
                    );
                }
            }

            if (originalAutoCommit) {
                connection.commit();
            }

            return true;

        } catch (Exception e) {
            if (originalAutoCommit) {
                connection.rollback();
            } else if (savepoint != null) {
                connection.rollback(savepoint);
            }
            throw e;

        } finally {
            if (originalAutoCommit) {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<Map<String, Object>> getCompanyOffers(
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

        List<Map<String, Object>> offers = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("offer_id", rs.getInt("offer_id"));
                    row.put("application_id", rs.getInt("application_id"));
                    row.put("offer_date", rs.getTimestamp("offer_date"));
                    row.put("joining_date", rs.getDate("joining_date"));
                    row.put("status", rs.getString("status"));
                    row.put("student_name", rs.getString("student_name"));
                    row.put("email", rs.getString("email"));
                    row.put("role", rs.getString("role"));

                    offers.add(row);
                }
            }
        }

        return offers;
    }


    public List<Map<String, Object>> getStudentOffers(
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
            c.company_name AS company_name,
            i.role
        FROM offers o
        JOIN applications a
            ON o.application_id = a.application_id
        JOIN internships i
            ON a.intern_id = i.intern_id
        LEFT JOIN companies c
            ON i.company_id = c.company_id
        WHERE a.student_id = ?
        ORDER BY o.offer_date DESC
        """;

        List<Map<String, Object>> offers = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("offer_id", rs.getInt("offer_id"));
                    row.put("application_id", rs.getInt("application_id"));
                    row.put("offer_date", rs.getTimestamp("offer_date"));
                    row.put("joining_date", rs.getDate("joining_date"));
                    row.put("status", rs.getString("status"));
                    row.put("company_name", rs.getString("company_name"));
                    row.put("role", rs.getString("role"));

                    offers.add(row);
                }
            }
        }

        return offers;
    }


    public List<Map<String, Object>> getAllOffers(
            Connection connection
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
                i.company_name,
                i.role
            FROM offers o
            JOIN applications a ON o.application_id = a.application_id
            JOIN students s ON a.student_id = s.student_id
            JOIN internships i ON a.intern_id = i.intern_id
            ORDER BY o.offer_date DESC
            """;

        List<Map<String, Object>> offers = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                row.put("offer_id", rs.getInt("offer_id"));
                row.put("application_id", rs.getInt("application_id"));
                row.put("offer_date", rs.getTimestamp("offer_date"));
                row.put("joining_date", rs.getDate("joining_date"));
                row.put("status", rs.getString("status"));
                row.put("student_name", rs.getString("student_name"));
                row.put("email", rs.getString("email"));
                row.put("company_name", rs.getString("company_name"));
                row.put("role", rs.getString("role"));

                offers.add(row);
            }
        }

        return offers;
    }

    public void updateOfferStatus(
            Connection connection,
            int offerId,
            int studentId,
            String status
    ) throws Exception {

        if (!List.of("Accepted", "Rejected").contains(status)) {
            throw new IllegalArgumentException("Invalid offer status.");
        }

        String applicationStatus = status.equals("Accepted")
                ? "Offer Accepted"
                : "Offer Rejected";

        boolean originalAutoCommit = connection.getAutoCommit();
        Savepoint savepoint = null;

        try {
            if (originalAutoCommit) {
                connection.setAutoCommit(false);
            } else {
                savepoint = connection.setSavepoint();
            }

            String offerSql = """
                UPDATE offers o
                JOIN applications a ON o.application_id = a.application_id
                SET o.status = ?
                WHERE o.offer_id = ?
                  AND a.student_id = ?
                  AND o.status = 'Pending'
                """;

            try (PreparedStatement ps =
                         connection.prepareStatement(offerSql)) {
                ps.setString(1, status);
                ps.setInt(2, offerId);
                ps.setInt(3, studentId);

                if (ps.executeUpdate() == 0) {
                    throw new Exception(
                            "Offer not found or it has already been processed."
                    );
                }
            }

            String applicationSql = """
                UPDATE applications a
                JOIN offers o ON o.application_id = a.application_id
                SET a.status = ?
                WHERE o.offer_id = ?
                  AND a.student_id = ?
                """;

            try (PreparedStatement ps =
                         connection.prepareStatement(applicationSql)) {
                ps.setString(1, applicationStatus);
                ps.setInt(2, offerId);
                ps.setInt(3, studentId);

                if (ps.executeUpdate() == 0) {
                    throw new Exception(
                            "Could not update the related application."
                    );
                }
            }

            if (originalAutoCommit) {
                connection.commit();
            }

        } catch (Exception e) {
            if (originalAutoCommit) {
                connection.rollback();
            } else if (savepoint != null) {
                connection.rollback(savepoint);
            }
            throw e;

        } finally {
            if (originalAutoCommit) {
                connection.setAutoCommit(true);
            }
        }
    }
}


