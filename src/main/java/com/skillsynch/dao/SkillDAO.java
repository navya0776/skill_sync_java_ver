package com.skillsynch.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillDAO {

    // 1. GET ALL AVAILABLE SKILLS
    public List<Map<String, Object>> getAllSkills(
            Connection connection
    ) throws Exception {

        String sql = """
                SELECT skill_id, skill_name, category
                FROM Skills
                ORDER BY skill_name
                """;

        List<Map<String, Object>> skills = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Map<String, Object> skill = new HashMap<>();

                skill.put("skill_id",
                        result.getInt("skill_id"));

                skill.put("skill_name",
                        result.getString("skill_name"));

                skill.put("category",
                        result.getString("category"));

                skills.add(skill);
            }
        }

        return skills;
    }


    // 2. GET SKILLS BELONGING TO A PARTICULAR STUDENT
    public List<Map<String, Object>> getStudentSkills(
            Connection connection,
            int studentId
    ) throws Exception {

        String sql = """
                SELECT s.skill_id,
                       s.skill_name,
                       s.category,
                       ss.proficiency_level
                FROM Student_Skills ss
                JOIN Skills s
                ON ss.skill_id = s.skill_id
                WHERE ss.student_id = ?
                ORDER BY s.skill_name
                """;

        List<Map<String, Object>> skills = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Map<String, Object> skill = new HashMap<>();

                    skill.put("skill_id",
                            result.getInt("skill_id"));

                    skill.put("skill_name",
                            result.getString("skill_name"));

                    skill.put("category",
                            result.getString("category"));

                    skill.put("proficiency_level",
                            result.getInt("proficiency_level"));

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
                INSERT INTO Student_Skills
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

        String sql = "INSERT INTO Skills (skill_name, category) VALUES (?, ?)";

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
                DELETE FROM Student_Skills
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
