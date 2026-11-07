package com.scc.dao;

import com.scc.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ViolationDao {

    public boolean issueViolation(String licenseNo, String violationType, double fineAmount, int officerId) {
        String sql = """
            INSERT INTO violations (license_no, violation_type, fine_amount, payment_status, issued_by)
            VALUES (?, ?, ?, 'PENDING', ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licenseNo);
            pstmt.setString(2, violationType);
            pstmt.setDouble(3, fineAmount);
            pstmt.setInt(4, officerId);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to issue violation: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int violationId, String oldStatus, String newStatus, int officerId) {
        String sql = """
            UPDATE violations 
            SET payment_status = ? 
            WHERE violation_id = ? AND payment_status = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus.toUpperCase());
            pstmt.setInt(2, violationId);
            pstmt.setString(3, oldStatus.toUpperCase());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Failed to update violation status: " + e.getMessage());
            return false;
        }
    }
}