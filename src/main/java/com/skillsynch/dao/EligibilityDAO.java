
package com.skillsynch.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EligibilityDAO {

    public void evaluateAndSave(
            Connection connection,
            int studentId,
            int internId
    ) throws Exception {

        // Get student CGPA and internship minimum CGPA
        String cgpaSql = """
            SELECT s.cgpa, i.min_cgpa
            FROM students s
            CROSS JOIN internships i
            WHERE s.student_id = ? AND i.intern_id = ?
            """;

        double cgpa;
        double minCgpa;

        try (PreparedStatement ps = connection.prepareStatement(cgpaSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("Student or internship not found.");
                }

                cgpa = rs.getDouble("cgpa");
                minCgpa = rs.getDouble("min_cgpa");
            }
        }

        // Calculate matching skills
        String skillSql = """
            SELECT
                COUNT(*) AS total_skills,
                SUM(
                    CASE WHEN ss.proficiency_level >= isk.min_level
                    THEN 1 ELSE 0 END
                ) AS matched_skills
            FROM internship_skills isk
            LEFT JOIN student_skills ss
                ON isk.skill_id = ss.skill_id
                AND ss.student_id = ?
            WHERE isk.intern_id = ?
            """;

        int totalSkills;
        int matchedSkills;

        try (PreparedStatement ps = connection.prepareStatement(skillSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                totalSkills = rs.getInt("total_skills");
                matchedSkills = rs.getInt("matched_skills");
            }
        }

        double matchPercentage = totalSkills == 0
                ? 0
                : (matchedSkills * 100.0 / totalSkills);

        String eligibilityStatus;

        if (cgpa < minCgpa) {
            eligibilityStatus = "Not Eligible";
        } else if (matchPercentage >= 70) {
            eligibilityStatus = "Eligible";
        } else if (matchPercentage >= 40) {
            eligibilityStatus = "Partially Eligible";
        } else {
            eligibilityStatus = "Not Eligible";
        }

        // Save or update eligibility
        String saveSql = """
            INSERT INTO eligibility
                (student_id, intern_id, match_percentage, eligibility_status)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                match_percentage = VALUES(match_percentage),
                eligibility_status = VALUES(eligibility_status)
            """;

        try (PreparedStatement ps = connection.prepareStatement(saveSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);
            ps.setDouble(3, matchPercentage);
            ps.setString(4, eligibilityStatus);
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> getStudentEligibility(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
            SELECT e.intern_id, e.match_percentage,
                   e.eligibility_status,
                   i.company_name, i.role
            FROM eligibility e
            JOIN internships i ON e.intern_id = i.intern_id
            WHERE e.student_id = ?
            ORDER BY e.match_percentage DESC
            """;

        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    row.put("intern_id", rs.getInt("intern_id"));
                    row.put("match_percentage", rs.getDouble("match_percentage"));
                    row.put("eligibility_status", rs.getString("eligibility_status"));
                    row.put("company_name", rs.getString("company_name"));
                    row.put("role", rs.getString("role"));

                    results.add(row);
                }
            }
        }

        return results;
    }
}
