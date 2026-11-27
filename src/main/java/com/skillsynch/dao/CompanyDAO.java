package com.skillsynch.dao;

import com.skillsynch.model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

    public int createCompany(
            Connection connection,
            String companyName,
            String industry,
            String description
    ) throws Exception {

        String sql = """
                INSERT INTO companies
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

    public List<Company> getAllCompanies(
            Connection connection
    ) throws Exception {

        String sql = """
            SELECT company_id, company_name, industry, description
            FROM companies
            ORDER BY company_name
            """;

        List<Company> companies = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                companies.add(mapCompany(resultSet));
            }
        }

        return companies;
    }

    // Returns null if the company does not exist
    public Company getCompanyById(
            Connection connection,
            int companyId
    ) throws Exception {

        String sql = """
            SELECT company_id, company_name, industry, description
            FROM companies
            WHERE company_id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, companyId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapCompany(resultSet);
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
            UPDATE companies
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

    // Turns the current ResultSet row into a Company
    private Company mapCompany(ResultSet rs) throws SQLException {

        return new Company(
                rs.getInt("company_id"),
                rs.getString("company_name"),
                rs.getString("industry"),
                rs.getString("description")
        );
    }
}