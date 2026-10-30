package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner sc;

    public AdminMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
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
            System.out.println("7. Logout");
            System.out.print("Choose an option (1-7): ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewPendingUsers();
                    break;
                case "3":
                    approveUserRegistration();
                    break;
                case "4":
                    addNewUserByAdmin();
                    break;
                case "5":
                    updateUserRoleAndStatus(adminUser);
                    break;
                case "6":
                    deleteUserAccount(adminUser);
                    break;
                case "7":
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

    private void approveUserRegistration() {
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
    }

    private void addNewUserByAdmin() {
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

        // Self-Edit Restriction Rule Check
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
    }

    // Input Validation Wrappers
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