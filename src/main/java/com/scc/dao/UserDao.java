package com.scc.dao;

import com.scc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public boolean registerUser(String username, String password, String role) {
        String sql = """
            INSERT INTO users (username, password, roles, status)
            VALUES (?, ?, ?, 'PENDING')
        """;

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());

            pstmt.executeUpdate();
            System.out.println("Registration successful! Your account status is PENDING approval.");
            return true;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public boolean addUserDirectly(String username, String password, String role, String status) {
        String sql = """
            INSERT INTO users (username, password, roles, status)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());
            pstmt.setString(4, status.toUpperCase());

            pstmt.executeUpdate();
            System.out.println("New user added successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("Failed to add user: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String sql = """
            SELECT id, username, roles, status FROM users
            WHERE username = ? AND password = ?
        """;

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if (!"ACTIVE".equalsIgnoreCase(status) && !"APPROVED".equalsIgnoreCase(status)) {
                        System.out.println("Login denied. Account status is: " + status);
                        return null;
                    }
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            "",
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        System.out.println("Invalid username or password.");
        return null;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT id, username, password, roles, status FROM users";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return userList;
    }

    public List<User> getPendingUsers() {
        List<User> pendingList = new ArrayList<>();
        String sql = "SELECT id, username, password, roles, status FROM users WHERE status = 'PENDING'";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                pendingList.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return pendingList;
    }

    public boolean approveUser(int userId) {
        String sql = "UPDATE users SET status = 'ACTIVE' WHERE id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("User ID " + userId + " approved successfully.");
                return true;
            } else {
                System.out.println("User ID not found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error approving users: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserRoleAndStatus(int userId, String newRole, String newStatus) {
        String sql = "UPDATE users SET roles = ?, status = ? WHERE id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole.toUpperCase());
            pstmt.setString(2, newStatus.toUpperCase());
            pstmt.setInt(3, userId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User ID " + userId + " updated successfully.");
                return true;
            } else {
                System.out.println("User ID not found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("User ID " + userId + " deleted successfully.");
                return true;
            } else {
                System.out.println("User ID not found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}