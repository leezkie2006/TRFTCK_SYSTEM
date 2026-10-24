package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n==========================================");
            System.out.println("          TRAFFIC TRACKING SYSTEM         ");
            System.out.println("==========================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegistration();
                    break;
                case "3":
                    exit = true;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void handleLogin() {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User loggedInUser = userDao.loginUser(username, password);

        if (loggedInUser != null) {
            System.out.println("Login successful!");

            // Route based on user role
            if ("ADMIN".equalsIgnoreCase(loggedInUser.roles())) {
                AdminMenu adminMenu = new AdminMenu(userDao, sc);
                adminMenu.display(loggedInUser);
            } else {
                // Regular user view or menu placeholder
                System.out.println("Welcome, " + loggedInUser.username() + "! (User dashboard coming soon)");
            }
        }
    }

    private void handleRegistration() {
        System.out.println("\n--- USER REGISTRATION ---");
        System.out.print("Enter desired username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        System.out.print("Enter role (ADMIN / USER): ");
        String role = sc.nextLine();

        userDao.registerUser(username, password, role);
    }
}