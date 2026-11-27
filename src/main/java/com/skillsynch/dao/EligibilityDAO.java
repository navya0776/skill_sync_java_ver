package com.skillsynch.dao;

import com.skillsynch.model.Eligibility;
import com.skillsynch.model.EligibilityStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

        Double cgpa;
        Double minCgpa;

        try (PreparedStatement ps = connection.prepareStatement(cgpaSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("Student or internship not found.");
                }

                cgpa = DaoUtil.getNullableDouble(rs, "cgpa");
                minCgpa = DaoUtil.getNullableDouble(rs, "min_cgpa");
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
                ? 100


                : (matchedSkills * 100.0 / totalSkills);

        EligibilityStatus eligibilityStatus;

        if (minCgpa != null && (cgpa == null || cgpa < minCgpa)) {
            eligibilityStatus = EligibilityStatus.NOT_ELIGIBLE;
        } else if (matchPercentage >= 70) {
            eligibilityStatus = EligibilityStatus.ELIGIBLE;
        } else if (matchPercentage >= 40) {
            eligibilityStatus = EligibilityStatus.PARTIALLY_ELIGIBLE;
        } else {
            eligibilityStatus = EligibilityStatus.NOT_ELIGIBLE;
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

            ps.setString(4, eligibilityStatus.getLabel());
            ps.executeUpdate();
        }
    }

    // Company name and role are filled in from the internships table
    public List<Eligibility> getStudentEligibility(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
            SELECT e.student_id, e.intern_id, e.match_percentage,
                   e.eligibility_status,
                   i.company_name, i.role
            FROM eligibility e
            JOIN internships i ON e.intern_id = i.intern_id
            WHERE e.student_id = ?
            ORDER BY e.match_percentage DESC
            """;

        List<Eligibility> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Eligibility eligibility = new Eligibility(
                            rs.getInt("student_id"),
                            rs.getInt("intern_id"),
                            rs.getDouble("match_percentage"),
                            EligibilityStatus.fromLabel(rs.getString("eligibility_status"))
                    );

                    eligibility.setCompanyName(rs.getString("company_name"));
                    eligibility.setRole(rs.getString("role"));

                    results.add(eligibility);
                }
            }
        }

        return results;
    }
}