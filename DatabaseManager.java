package com.emicalculator.database;

import com.emicalculator.model.EMICalculation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:emi_calculator.db";
    private Connection connection;

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS emi_history (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "principal REAL NOT NULL, " +
                     "rate_of_interest REAL NOT NULL, " +
                     "tenure_months INTEGER NOT NULL, " +
                     "emi_amount REAL NOT NULL, " +
                     "total_amount REAL NOT NULL, " +
                     "total_interest REAL NOT NULL, " +
                     "calculated_at TEXT NOT NULL)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public void saveCalculation(EMICalculation calculation) {
        String sql = "INSERT INTO emi_history (principal, rate_of_interest, tenure_months, " +
                     "emi_amount, total_amount, total_interest, calculated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, calculation.getPrincipal());
            pstmt.setDouble(2, calculation.getRateOfInterest());
            pstmt.setInt(3, calculation.getTenureMonths());
            pstmt.setDouble(4, calculation.getEmiAmount());
            pstmt.setDouble(5, calculation.getTotalAmount());
            pstmt.setDouble(6, calculation.getTotalInterest());
            pstmt.setString(7, calculation.getCalculatedAt().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving calculation: " + e.getMessage());
        }
    }

    public List<EMICalculation> getCalculationHistory() {
        List<EMICalculation> history = new ArrayList<>();
        String sql = "SELECT * FROM emi_history ORDER BY calculated_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                EMICalculation calc = new EMICalculation();
                calc.setId(rs.getInt("id"));
                calc.setPrincipal(rs.getDouble("principal"));
                calc.setRateOfInterest(rs.getDouble("rate_of_interest"));
                calc.setTenureMonths(rs.getInt("tenure_months"));
                calc.setEmiAmount(rs.getDouble("emi_amount"));
                calc.setTotalAmount(rs.getDouble("total_amount"));
                calc.setTotalInterest(rs.getDouble("total_interest"));
                calc.setCalculatedAt(LocalDateTime.parse(rs.getString("calculated_at")));
                history.add(calc);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving history: " + e.getMessage());
        }
        
        return history;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}
