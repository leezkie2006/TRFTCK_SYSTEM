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
        boolean running = true;
        while (running) {
            System.out.println("\n==========================================");
            System.out.println("        TRAFFIC TRACKING SYSTEM           ");
            System.out.println("==========================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    System.out.print("Enter new username: ");
                    String user = sc.nextLine();
                    System.out.print("Enter password: ");
                    String pass = sc.nextLine();

                    System.out.print("Select Role [1] OFFICER | [2] ADMIN: ");
                    int roleChoice = sc.nextInt();
                    sc.nextLine(); // Clear scanner buffer

                    String role;
                    switch (roleChoice) {
                        case 1:
                            role = "OFFICER";
                            break;
                        case 2:
                            role = "ADMIN";
                            break;
                        default:
                            role = "OFFICER";
                            break;
                    }

                    userDao.registerUser(user, pass, role);
                    break;
                case "3":
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void login() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User user = userDao.loginUser(username, password);

        if (user != null) {
            System.out.println("\n -> Welcome " + user.username() + " | ROLE: " + user.roles() + "");
            switch (user.roles()) {
                case "ADMIN" -> new AdminMenu(userDao, sc).display(user);
                case "OFFICER" -> System.out.println("Officer menu coming soon!");
                default -> System.out.println("Account roles not found!");
            }
        }
    }
}