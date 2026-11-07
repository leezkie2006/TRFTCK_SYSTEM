package com.scc.ui;

import com.scc.dao.ViolationDao;
import com.scc.dao.ActivityLogDao;
import com.scc.model.User;

import java.util.Scanner;

public class OfficerMenu {
    private final ViolationDao violationDao;
    private final ActivityLogDao logDao;
    private final Scanner sc;

    public OfficerMenu(ViolationDao violationDao, ActivityLogDao logDao, Scanner sc) {
        this.violationDao = violationDao;
        this.logDao = logDao;
        this.sc = sc;
    }

    public void display(User officerUser) {
        boolean back = false;
        while (!back) {
            System.out.println("\n-> Welcome Officer " + officerUser.username() + " | ROLE: " + officerUser.roles());
            System.out.println("==========================================================");
            System.out.println("                     OFFICER DASHBOARD                    ");
            System.out.println("==========================================================");
            System.out.println("1. Issue Traffic Violation (Core Business Process)");
            System.out.println("2. Update Violation Status (Lifecycle Change)");
            System.out.println("3. Logout");
            System.out.print("Choose an option (1-3): ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    issueViolationProcess(officerUser);
                    break;
                case "2":
                    updateViolationStatusProcess(officerUser);
                    break;
                case "3":
                    back = true;
                    System.out.println("Logging out from Officer Menu...");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void issueViolationProcess(User officer) {
        System.out.println("\n--- ISSUE TRAFFIC VIOLATION ---");
        System.out.print("Enter License Number (0 to cancel): ");
        String licenseNo = sc.nextLine().trim();
        if ("0".equals(licenseNo)) {
            System.out.println("Operation cancelled.");
            return;
        }

        System.out.print("Enter Violation Type (e.g., Speeding, No Helmet): ");
        String violationType = sc.nextLine().trim();

        double fineAmount = 0.0;
        while (true) {
            System.out.print("Enter Fine Amount (₱): ");
            try {
                fineAmount = Double.parseDouble(sc.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount! Please enter a valid number.");
            }
        }

        boolean success = violationDao.issueViolation(licenseNo, violationType, fineAmount, officer.id());
        if (success) {
            System.out.println("-> Success: Issued citation for " + violationType + " to License #" + licenseNo + " - Fine: ₱" + fineAmount);
        } else {
            System.out.println("-> Error: Failed to issue citation.");
        }
    }

    private void updateViolationStatusProcess(User officer) {
        System.out.println("\n--- UPDATE VIOLATION STATUS ---");
        int violationId = readIntInput("Enter Violation ID to update (0 to cancel): ");
        if (violationId == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        System.out.print("Enter Old Status (e.g., PENDING): ");
        String oldStatus = sc.nextLine().trim().toUpperCase();

        System.out.print("Enter New Status (e.g., PAID, RESOLVED): ");
        String newStatus = sc.nextLine().trim().toUpperCase();

        boolean success = violationDao.updateStatus(violationId, oldStatus, newStatus, officer.id());
        if (success) {
            System.out.println("-> Success: Updated Traffic Violation #" + violationId + " status from " + oldStatus + " to " + newStatus);
        } else {
            System.out.println("-> Error: Failed to update status.");
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }
}