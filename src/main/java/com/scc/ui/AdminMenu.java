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
            System.out.println("\n==========================================");
            System.out.println("          ADMIN MENU - " + adminUser.username());
            System.out.println("==========================================");
            System.out.println("1. View All Users");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
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