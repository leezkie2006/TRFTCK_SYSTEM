package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.dao.ViolationDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, ActivityLogDao logDao, Scanner sc) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.sc = sc;
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n==========================================================");
            System.out.println("            ONLINE TRAFFIC VIOLATION SYSTEM               ");
            System.out.println("==========================================================");
            System.out.println("1. Login");
            System.out.println("2. Register New Account");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3): ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegistration();
                    break;
                case "3":
                    exit = true;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void handleLogin() {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        User user = userDao.loginUser(username, password);
        if (user != null) {
            logDao.logAction(user.id(), "USER_LOGIN", "User logged into the system.");
            System.out.println("Login successful!");

            String role = user.roles().toUpperCase();
            if ("ADMIN".equals(role)) {
                AdminMenu adminMenu = new AdminMenu(userDao, logDao, sc);
                adminMenu.display(user);
            } else if ("OFFICER".equals(role)) {
                ViolationDao violationDao = new ViolationDao();
                OfficerMenu officerMenu = new OfficerMenu(violationDao, logDao, sc);
                officerMenu.display(user);
            } else {
                System.out.println("Access restricted: Role '" + role + "' dashboard is not available.");
            }
        }
    }

    private void handleRegistration() {
        System.out.println("\n--- USER REGISTRATION ---");
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();
        System.out.print("Enter role (ADMIN, OFFICER, USER): ");
        String role = sc.nextLine().trim().toUpperCase();

        if (role.isEmpty()) {
            role = "USER";
        }

        userDao.registerUser(username, password, role);
    }
}