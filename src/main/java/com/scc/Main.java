package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao; // <-- ADD THIS LINE
import com.scc.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // 1. Initialize database tables & verify constraints
        DatabaseInitializer.initialize();

        // 2. Instantiate shared DAO and Scanner utilities to prevent duplication
        Scanner scanner = new Scanner(System.in);
        UserDao userDao = new UserDao();

        // 3. Delegate execution flow to Authentication UI handler
        Authentication auth = new Authentication(userDao, scanner);
        auth.start();

        scanner.close();
    }
}