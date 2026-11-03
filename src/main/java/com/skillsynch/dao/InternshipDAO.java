
package com.skillsynch.dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.ResultSet;

public class InternshipDAO {

    public int createInternship(
            Connection connection,
            int companyId,
            String companyName,
            String role,
            String description,
            double minCgpa,
            int numberOfPositions,
            Date applicationDeadline
    ) throws Exception {

        String sql = """
                INSERT INTO Internships
                (company_id, company_name, role, description,
                 min_cgpa, number_of_positions,
                 application_deadline, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, companyId);
            statement.setString(2, companyName);
            statement.setString(3, role);
            statement.setString(4, description);
            statement.setDouble(5, minCgpa);
            statement.setInt(6, numberOfPositions);
            statement.setDate(7, applicationDeadline);
            statement.setString(8, "Pending");

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new Exception("Internship creation failed.");
    }

    public void updateInternshipStatus(
            Connection connection,
            int internshipId,
            String status
    ) throws Exception {

        if (!status.equals("Approved") &&
                !status.equals("Rejected") &&
                !status.equals("Published") &&
                !status.equals("Closed")) {

            throw new IllegalArgumentException("Invalid internship status.");
        }

        String sql = """
            UPDATE Internships
            SET status = ?
            WHERE intern_id = ?
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, internshipId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new Exception("Internship not found.");
            }
        }
    }

    public List<Map<String, Object>> getAllInternships(
            Connection connection
    ) throws Exception {

        String sql = """
            SELECT intern_id, company_id, company_name, role,
                   description, min_cgpa, number_of_positions,
                   application_deadline, status
            FROM Internships
            ORDER BY intern_id DESC
            """;

        List<Map<String, Object>> internships = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Map<String, Object> internship = new HashMap<>();

                internship.put("intern_id", resultSet.getInt("intern_id"));
                internship.put("company_id", resultSet.getInt("company_id"));
                internship.put("company_name", resultSet.getString("company_name"));
                internship.put("role", resultSet.getString("role"));
                internship.put("description", resultSet.getString("description"));
                internship.put("min_cgpa", resultSet.getDouble("min_cgpa"));
                internship.put("number_of_positions",
                        resultSet.getInt("number_of_positions"));
                internship.put("application_deadline",
                        resultSet.getDate("application_deadline"));
                internship.put("status", resultSet.getString("status"));

                internships.add(internship);
            }
        }

        return internships;
    }
    public void addInternshipSkill(
            Connection connection,
            int internshipId,
            int skillId,
            int minLevel
    ) throws Exception {

        if (minLevel < 1 || minLevel > 5) {
            throw new IllegalArgumentException(
                    "Minimum skill level must be between 1 and 5."
            );
        }

        String sql = """
            INSERT INTO Internship_Skills
            (intern_id, skill_id, min_level)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, internshipId);
            statement.setInt(2, skillId);
            statement.setInt(3, minLevel);

            statement.executeUpdate();
        }
    }
    public void addRequiredSkills(
            Connection connection,
            int internshipId,
            Map<Integer, Integer> requiredSkills
    ) throws Exception {

        String sql = """
            INSERT INTO Internship_Skills
            (intern_id, skill_id, min_level)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Map.Entry<Integer, Integer> entry : requiredSkills.entrySet()) {

                int skillId = entry.getKey();
                int minLevel = entry.getValue();

                if (minLevel < 1 || minLevel > 5) {
                    throw new IllegalArgumentException(
                            "Minimum skill level must be between 1 and 5."
                    );
                }

                statement.setInt(1, internshipId);
                statement.setInt(2, skillId);
                statement.setInt(3, minLevel);

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
    public List<Map<String, Object>> getCompanyInternships(
            Connection connection,
            int companyId
    ) throws Exception {

        String sql = """
        SELECT intern_id, company_id, company_name, role,
               description, min_cgpa, number_of_positions,
               application_deadline, status
        FROM Internships
        WHERE company_id = ?
        ORDER BY intern_id DESC
        """;

        List<Map<String, Object>> internships = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, companyId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Map<String, Object> internship = new HashMap<>();

                    internship.put("intern_id", resultSet.getInt("intern_id"));
                    internship.put("company_id", resultSet.getInt("company_id"));
                    internship.put("company_name", resultSet.getString("company_name"));
                    internship.put("role", resultSet.getString("role"));
                    internship.put("description", resultSet.getString("description"));
                    internship.put("min_cgpa", resultSet.getDouble("min_cgpa"));
                    internship.put("number_of_positions",
                            resultSet.getInt("number_of_positions"));
                    internship.put("application_deadline",
                            resultSet.getDate("application_deadline"));
                    internship.put("status", resultSet.getString("status"));

                    internships.add(internship);
                }
            }
        }

        return internships;
    }
}
