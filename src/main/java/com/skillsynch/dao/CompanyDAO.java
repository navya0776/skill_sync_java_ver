
package com.skillsynch.dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompanyDAO {

    public int createCompany(
            Connection connection,
            String companyName,
            String industry,
            String description
    ) throws Exception {

        String sql = """
                INSERT INTO Companies
                (company_name, industry, description)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, companyName);
            statement.setString(2, industry);
            statement.setString(3, description);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new Exception("Company creation failed.");
    }
    public List<Map<String, Object>> getAllCompanies(
            Connection connection
    ) throws Exception {

        String sql = """
            SELECT company_id, company_name, industry, description
            FROM Companies
            ORDER BY company_name
            """;

        List<Map<String, Object>> companies = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Map<String, Object> company = new HashMap<>();

                company.put("company_id", resultSet.getInt("company_id"));
                company.put("company_name", resultSet.getString("company_name"));
                company.put("industry", resultSet.getString("industry"));
                company.put("description", resultSet.getString("description"));

                companies.add(company);
            }
        }

        return companies;
    }
    public Map<String, Object> getCompanyById(
            Connection connection,
            int companyId
    ) throws Exception {

        String sql = """
        SELECT company_id, company_name, industry, description
        FROM Companies
        WHERE company_id = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, companyId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Map<String, Object> company = new HashMap<>();

                    company.put("company_id", resultSet.getInt("company_id"));
                    company.put("company_name", resultSet.getString("company_name"));
                    company.put("industry", resultSet.getString("industry"));
                    company.put("description", resultSet.getString("description"));

                    return company;
                }
            }
        }

        return null;
    }

    public boolean updateCompany(
            Connection connection,
            int companyId,
            String companyName,
            String industry,
            String description
    ) throws Exception {

        String sql = """
        UPDATE Companies
        SET company_name = ?, industry = ?, description = ?
        WHERE company_id = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, companyName);
            statement.setString(2, industry);
            statement.setString(3, description);
            statement.setInt(4, companyId);

            return statement.executeUpdate() > 0;
        }
    }
}
