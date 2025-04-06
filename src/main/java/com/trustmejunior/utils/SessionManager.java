package com.trustmejunior.utils;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.User.Account;

public class SessionManager {
    private static Account currentAccount;

    private SessionManager() {
        // Private constructor to prevent instantiation
    }

    public static void setCurrentAccount(Account account) {
        currentAccount = account;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static void clearSession() {
        currentAccount = null;
    }

    public static boolean isLoggedIn() {
        return currentAccount != null;
    }
}