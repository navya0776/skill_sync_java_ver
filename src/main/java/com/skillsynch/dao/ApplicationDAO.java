
package com.skillsynch.dao;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

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
            i.company_name,
            i.role
        FROM applications a
        JOIN internships i ON a.intern_id = i.intern_id
        WHERE a.student_id = ?
        ORDER BY a.application_date DESC
        """;

        List<Map<String, Object>> applications = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> application = new HashMap<>();

                    application.put("application_id", rs.getInt("application_id"));
                    application.put("intern_id", rs.getInt("intern_id"));
                    application.put("status", rs.getString("status"));
                    application.put("application_date", rs.getTimestamp("application_date"));
                    application.put("company_name", rs.getString("company_name"));
                    application.put("role", rs.getString("role"));

                    applications.add(application);
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

                    Map<String, Object> application = new HashMap<>();

                    application.put("application_id", rs.getInt("application_id"));
                    application.put("student_id", rs.getInt("student_id"));
                    application.put("intern_id", rs.getInt("intern_id"));
                    application.put("status", rs.getString("status"));
                    application.put("application_date", rs.getTimestamp("application_date"));
                    application.put("student_name", rs.getString("student_name"));
                    application.put("email", rs.getString("email"));
                    application.put("branch", rs.getString("branch"));
                    application.put("cgpa", rs.getDouble("cgpa"));
                    application.put("role", rs.getString("role"));

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
            String status
    ) throws Exception {

        if (!List.of("Shortlisted", "Interview", "Selected", "Rejected")
                .contains(status)) {
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

            ps.setString(1, status);
            ps.setInt(2, applicationId);
            ps.setInt(3, companyId);

            if (ps.executeUpdate() == 0) {
                throw new Exception("Application not found for this company.");
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

                    Map<String, Object> student = new HashMap<>();

                    student.put("application_id", rs.getInt("application_id"));
                    student.put("student_id", rs.getInt("student_id"));
                    student.put("status", rs.getString("status"));
                    student.put("application_date", rs.getTimestamp("application_date"));
                    student.put("student_name", rs.getString("student_name"));
                    student.put("email", rs.getString("email"));
                    student.put("branch", rs.getString("branch"));
                    student.put("cgpa", rs.getDouble("cgpa"));
                    student.put("role", rs.getString("role"));

                    students.add(student);
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
            i.company_name,
            i.role
        FROM applications a
        JOIN students s ON a.student_id = s.student_id
        JOIN internships i ON a.intern_id = i.intern_id
        ORDER BY a.application_date DESC
        """;

        List<Map<String, Object>> applications = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Map<String, Object> application = new HashMap<>();

                application.put("application_id", rs.getInt("application_id"));
                application.put("student_id", rs.getInt("student_id"));
                application.put("intern_id", rs.getInt("intern_id"));
                application.put("status", rs.getString("status"));
                application.put("application_date", rs.getTimestamp("application_date"));
                application.put("student_name", rs.getString("student_name"));
                application.put("email", rs.getString("email"));
                application.put("branch", rs.getString("branch"));
                application.put("cgpa", rs.getDouble("cgpa"));
                application.put("company_name", rs.getString("company_name"));
                application.put("role", rs.getString("role"));

                applications.add(application);
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
            SUM(CASE WHEN status = 'Selected' THEN 1 ELSE 0 END) AS selected,
            SUM(CASE WHEN status = 'Shortlisted' THEN 1 ELSE 0 END) AS shortlisted,
            SUM(CASE WHEN status = 'Rejected' THEN 1 ELSE 0 END) AS rejected,
            SUM(CASE WHEN status = 'Offer Extended' THEN 1 ELSE 0 END) AS offers_extended
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

        String checkSql = """
        SELECT a.application_id
        FROM applications a
        JOIN internships i ON a.intern_id = i.intern_id
        WHERE a.application_id = ?
        AND i.company_id = ?
        AND a.status = 'Selected'
        """;

        try (PreparedStatement check =
                     connection.prepareStatement(checkSql)) {

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

        try (PreparedStatement ps =
                     connection.prepareStatement(insertSql)) {

            ps.setInt(1, applicationId);
            ps.setDate(2, joiningDate);

            return ps.executeUpdate() > 0;
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

                    Map<String, Object> offer = new HashMap<>();

                    offer.put("offer_id", rs.getInt("offer_id"));
                    offer.put("application_id", rs.getInt("application_id"));
                    offer.put("offer_date", rs.getTimestamp("offer_date"));
                    offer.put("joining_date", rs.getDate("joining_date"));
                    offer.put("status", rs.getString("status"));
                    offer.put("student_name", rs.getString("student_name"));
                    offer.put("email", rs.getString("email"));
                    offer.put("role", rs.getString("role"));

                    offers.add(offer);
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
            i.company_name,
            i.role
        FROM offers o
        JOIN applications a ON o.application_id = a.application_id
        JOIN internships i ON a.intern_id = i.intern_id
        WHERE a.student_id = ?
        ORDER BY o.offer_date DESC
        """;

        List<Map<String, Object>> offers = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Map<String, Object> offer = new HashMap<>();

                    offer.put("offer_id", rs.getInt("offer_id"));
                    offer.put("application_id", rs.getInt("application_id"));
                    offer.put("offer_date", rs.getTimestamp("offer_date"));
                    offer.put("joining_date", rs.getDate("joining_date"));
                    offer.put("status", rs.getString("status"));
                    offer.put("company_name", rs.getString("company_name"));
                    offer.put("role", rs.getString("role"));

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
            String status
    ) throws Exception {

        if (!List.of("Accepted", "Rejected").contains(status)) {
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

            ps.setString(1, status);
            ps.setInt(2, offerId);
            ps.setInt(3, studentId);

            if (ps.executeUpdate() == 0) {
                throw new Exception(
                        "Offer not found or it has already been processed."
                );
            }
        }
    }
}
