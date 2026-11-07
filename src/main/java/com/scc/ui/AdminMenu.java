package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final Scanner sc;

    public AdminMenu(UserDao userDao, ActivityLogDao logDao, Scanner sc) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.sc = sc;
    }

    public void display(User adminUser) {
        boolean back = false;
        while (!back) {
            System.out.println("\n-> Welcome " + adminUser.username() + " | ROLE: " + adminUser.roles());
            System.out.println("==========================================================");
            System.out.println("                      ADMIN DASHBOARD                     ");
            System.out.println("==========================================================");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending User Approvals");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role / Status");
            System.out.println("6. Delete User Account");
            System.out.println("7. View System Audit Logs");
            System.out.println("8. Logout");
            System.out.print("Choose an option (1-8): ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewPendingUsers();
                    break;
                case "3":
                    approveUserRegistration(adminUser);
                    break;
                case "4":
                    addNewUserByAdmin(adminUser);
                    break;
                case "5":
                    updateUserRoleAndStatus(adminUser);
                    break;
                case "6":
                    deleteUserAccount(adminUser);
                    break;
                case "7":
                    viewAuditLogsMenu();
                    break;
                case "8":
                    back = true;
                    System.out.println("Logging out from Admin Menu...");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void viewAllUsers() {
        List<User> users = userDao.getAllUsers();
        displayTableUser(users, "ALL REGISTERED USERS");
    }

    private void viewPendingUsers() {
        List<User> pendingUsers = userDao.getPendingUsers();
        displayTableUser(pendingUsers, "PENDING USER APPROVALS");
    }

    private void approveUserRegistration(User currentAdmin) {
        List<User> pendingUsers = userDao.getPendingUsers();
        displayTableUser(pendingUsers, "PENDING USER APPROVALS");

        if (pendingUsers.isEmpty()) {
            return;
        }

        int userId = readIntInput("Enter User ID to approve (0 to cancel): ");
        if (userId == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        userDao.approveUser(userId);
        logDao.logAction(
                currentAdmin.id(),
                "USER_APPROVED",
                String.format("Admin [%d] approved account for User ID %d", currentAdmin.id(), userId)
        );
        System.out.println("User ID " + userId + " approved successfully.");
    }

    private void addNewUserByAdmin(User currentAdmin) {
        System.out.println("\n--- ADD NEW USER ---");
        System.out.print("Enter username (0 to cancel): ");
        String username = sc.nextLine().trim();
        if ("0".equals(username)) {
            System.out.println("Operation cancelled.");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        String role = readValidRole();
        if ("0".equals(role)) {
            System.out.println("Operation cancelled.");
            return;
        }

        String status = readValidStatus();
        if ("0".equals(status)) {
            System.out.println("Operation cancelled.");
            return;
        }

        userDao.addUserDirectly(username, password, role, status);
        logDao.logAction(
                currentAdmin.id(),
                "USER_ADDED",
                String.format("Admin [%d] created user '%s' with role %s", currentAdmin.id(), username, role)
        );
    }

    private void updateUserRoleAndStatus(User currentAdmin) {
        List<User> users = userDao.getAllUsers();
        displayTableUser(users, "ALL REGISTERED USERS");

        if (users.isEmpty()) {
            return;
        }

        int targetUserId = readIntInput("Enter User ID to update (0 to cancel): ");
        if (targetUserId == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        if (targetUserId == currentAdmin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        String newRole = readValidRole();
        if ("0".equals(newRole)) {
            System.out.println("Operation cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if ("0".equals(newStatus)) {
            System.out.println("Operation cancelled.");
            return;
        }

        userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus);
        logDao.logAction(
                currentAdmin.id(),
                "USER_UPDATED",
                String.format("Admin [%d] changed role of User %d to %s", currentAdmin.id(), targetUserId, newRole)
        );
        System.out.println("User ID " + targetUserId + " updated successfully.");
    }

    private void deleteUserAccount(User currentAdmin) {
        List<User> users = userDao.getAllUsers();
        displayTableUser(users, "ALL REGISTERED USERS");

        if (users.isEmpty()) {
            return;
        }

        int targetUserId = readIntInput("Enter User ID to delete (0 to cancel): ");
        if (targetUserId == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        if (targetUserId == currentAdmin.id()) {
            System.out.println("WARNING: Deleting your own active admin account is restricted.");
            return;
        }

        userDao.deleteUser(targetUserId);
        logDao.logAction(
                currentAdmin.id(),
                "USER_DELETED",
                String.format("Admin [%d] deleted user account ID %d", currentAdmin.id(), targetUserId)
        );
    }

    private void viewAuditLogsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--------------------------------------------------------------");
            System.out.println("                    SYSTEM AUDIT LOG MENU                     ");
            System.out.println("--------------------------------------------------------------");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Clear Historical Logs");
            System.out.println("4. Back to Admin Menu");
            System.out.print("Choose an option (1-4): ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    displayLogTable(logDao.getAllLogs(), "ALL SYSTEM AUDIT LOGS");
                    break;
                case "2":
                    int filterUserId = readIntInput("Enter User ID to filter: ");
                    displayLogTable(logDao.getLogsByUserId(filterUserId), "LOGS FOR USER ID " + filterUserId);
                    break;
                case "3":
                    System.out.print("Are you sure you want to clear all audit logs? (Y/N): ");
                    String confirm = sc.nextLine().trim();
                    if ("Y".equalsIgnoreCase(confirm)) {
                        logDao.clearLogs();
                    } else {
                        System.out.println("Operation cancelled.");
                    }
                    break;
                case "4":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void displayLogTable(List<ActivityLog> logs, String title) {
        System.out.println("\n---------------------------------------------------------------------------------------------");
        System.out.println(" " + title);
        System.out.println("---------------------------------------------------------------------------------------------");

        if (logs.isEmpty()) {
            System.out.println(" No log records found.");
            System.out.println("---------------------------------------------------------------------------------------------");
            return;
        }

        System.out.printf("| %-5s | %-8s | %-18s | %-35s | %-20s |\n", "ID", "USER ID", "ACTION TYPE", "DESCRIPTION", "TIMESTAMP");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (ActivityLog log : logs) {
            System.out.printf("| %-5d | %-8d | %-18s | %-35s | %-20s |\n",
                    log.logId(),
                    log.userId(),
                    log.actionType(),
                    log.description(),
                    log.timestamp() != null ? log.timestamp().toString() : "N/A");
        }
        System.out.println("---------------------------------------------------------------------------------------------");
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid numeric ID.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter valid role (ADMIN, OFFICER, USER) [0 to cancel]: ");
            String role = sc.nextLine().trim().toUpperCase();
            if ("0".equals(role)) {
                return "0";
            }
            if ("ADMIN".equals(role) || "OFFICER".equals(role) || "USER".equals(role) || "STUDENT".equals(role) || "TEACHER".equals(role)) {
                return role;
            }
            System.out.println("Invalid role! Please enter a valid role (ADMIN, OFFICER, USER).");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter valid status (APPROVED, PENDING, ACTIVE) [0 to cancel]: ");
            String status = sc.nextLine().trim().toUpperCase();
            if ("0".equals(status)) {
                return "0";
            }
            if ("APPROVED".equals(status) || "PENDING".equals(status) || "ACTIVE".equals(status)) {
                return status;
            }
            System.out.println("Invalid status! Please enter a valid status (APPROVED, PENDING).");
        }
    }

    private void displayTableUser(List<User> users, String title) {
        System.out.println("\n--------------------------------------------------------------");
        System.out.println(" " + title);
        System.out.println("--------------------------------------------------------------");

        if (users.isEmpty()) {
            System.out.println(" No records found.");
            System.out.println("--------------------------------------------------------------");
            return;
        }

        System.out.printf("| %-5s | %-15s | %-10s | %-10s |\n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("--------------------------------------------------------------");

        for (User u : users) {
            System.out.printf("| %-5d | %-15s | %-10s | %-10s |\n",
                    u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println("--------------------------------------------------------------");
    }
}