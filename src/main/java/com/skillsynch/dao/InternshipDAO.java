package com.skillsynch.dao;

import com.skillsynch.model.Internship;
import com.skillsynch.model.InternshipStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            statement.setString(8, InternshipStatus.PENDING.getLabel());

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
            InternshipStatus status
    ) throws Exception {

        if (status == null || status == InternshipStatus.PENDING) {
            throw new IllegalArgumentException("Invalid internship status.");
        }

        String sql = """
            UPDATE Internships
            SET status = ?
            WHERE intern_id = ?
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status.getLabel());
            statement.setInt(2, internshipId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new Exception("Internship not found.");
            }
        }
    }

    public List<Internship> getAllInternships(
            Connection connection
    ) throws Exception {

        String sql = """
            SELECT intern_id, company_id, company_name, role,
                   description, min_cgpa, number_of_positions,
                   application_deadline, status
            FROM Internships
            ORDER BY intern_id DESC
            """;

        List<Internship> internships = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                internships.add(mapInternship(resultSet));
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

    public List<Internship> getCompanyInternships(
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

        List<Internship> internships = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, companyId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    internships.add(mapInternship(resultSet));
                }
            }
        }

        return internships;
    }

    // Turns the current ResultSet row into an Internship
    private Internship mapInternship(ResultSet rs) throws SQLException {

        return new Internship(
                rs.getInt("intern_id"),
                DaoUtil.getNullableInt(rs, "company_id"),
                rs.getString("company_name"),
                rs.getString("role"),
                rs.getString("description"),
                DaoUtil.getNullableDouble(rs, "min_cgpa"),
                DaoUtil.getNullableInt(rs, "number_of_positions"),
                DaoUtil.toLocalDate(rs.getDate("application_deadline")),
                InternshipStatus.fromLabel(rs.getString("status"))
        );
    }
}