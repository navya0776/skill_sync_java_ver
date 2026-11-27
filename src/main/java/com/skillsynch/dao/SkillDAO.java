package com.skillsynch.dao;

import com.skillsynch.model.Skill;
import com.skillsynch.model.StudentSkill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {

    // 1. GET ALL AVAILABLE SKILLS
    public List<Skill> getAllSkills(
            Connection connection
    ) throws Exception {

        String sql = """
                SELECT skill_id, skill_name, category
                FROM skills
                ORDER BY skill_name
                """;

        List<Skill> skills = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                skills.add(new Skill(
                        result.getInt("skill_id"),
                        result.getString("skill_name"),
                        result.getString("category")
                ));
            }
        }

        return skills;
    }

    // Required skills the student lacks, or has below the required level
    public List<String> getMissingSkills(Connection connection, int studentId, int internId)
            throws Exception {

        String sql = """
            SELECT s.skill_name, isk.min_level, ss.proficiency_level
            FROM internship_skills isk
            JOIN skills s ON s.skill_id = isk.skill_id
            LEFT JOIN student_skills ss
                   ON ss.skill_id = isk.skill_id AND ss.student_id = ?
            WHERE isk.intern_id = ?
              AND (ss.proficiency_level IS NULL
                   OR ss.proficiency_level < isk.min_level)
            ORDER BY s.skill_name
            """;

        List<String> missing = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, internId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int current = rs.getInt("proficiency_level");
                    boolean hasSkill = !rs.wasNull();
                    int required = rs.getInt("min_level");
                    String name = rs.getString("skill_name");

                    missing.add(hasSkill
                            ? name + " (improve level " + current + " to " + required + ")"
                            : name + " (need level " + required + ")");
                }
            }
        }
        return missing;
    }

    // 2. GET SKILLS BELONGING TO A PARTICULAR STUDENT
    // (skill name and category are filled in from the Skills table)
    public List<StudentSkill> getStudentSkills(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
                SELECT ss.student_id,
                       s.skill_id,
                       s.skill_name,
                       s.category,
                       ss.proficiency_level
                FROM student_skills ss
                JOIN skills s
                ON ss.skill_id = s.skill_id
                WHERE ss.student_id = ?
                ORDER BY s.skill_name
                """;

        List<StudentSkill> skills = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    StudentSkill skill = new StudentSkill(
                            result.getInt("student_id"),
                            result.getInt("skill_id"),
                            result.getInt("proficiency_level")
                    );

                    skill.setSkillName(result.getString("skill_name"));
                    skill.setCategory(result.getString("category"));

                    skills.add(skill);
                }
            }
        }

        return skills;
    }

    // 3. ADD A SKILL TO A STUDENT
    public void addStudentSkill(
            Connection connection,
            int studentId,
            int skillId,
            int proficiencyLevel
    ) throws Exception {

        if (proficiencyLevel < 1 || proficiencyLevel > 5) {
            throw new IllegalArgumentException(
                    "Proficiency level must be between 1 and 5."
            );
        }

        String sql = """
                INSERT INTO student_skills
                (student_id, skill_id, proficiency_level)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.setInt(2, skillId);
            statement.setInt(3, proficiencyLevel);

            statement.executeUpdate();
        }
    }

    public void addSkill(Connection conn, String skillName, String category)
            throws Exception {

        String sql = "INSERT INTO skills (skill_name, category) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, skillName);
            ps.setString(2, category);
            ps.executeUpdate();
        }
    }

    // 4. REMOVE A SKILL FROM A STUDENT
    public void removeStudentSkill(
            Connection connection,
            int studentId,
            int skillId
    ) throws Exception {

        String sql = """
                DELETE FROM student_skills
                WHERE student_id = ?
                AND skill_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.setInt(2, skillId);

            statement.executeUpdate();
        }
    }
}