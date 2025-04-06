package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.repository.AccountRepository;
import com.trustmejunior.request.CreateAccountRequest;
import com.trustmejunior.request.UpdateAccountRequest;
import com.trustmejunior.request.LoginRequest;

import java.util.Date;
import java.util.List;

public class AccountService {
    private static final int MIN_USERNAME_LENGTH = 5;
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 32;

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$";
    private static final String FULLNAME_REGEX = "[a-zA-Z\\s]+";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private AccountRepository accountRepository;

    public AccountService() {
        this.accountRepository = new AccountRepository();
    }

    // Logs in a user by validating the provided username and password
    public Account login(LoginRequest request) {
        return accountRepository.getAccount(request.getUsername(), request.getPassword());
    }

    // Retrieves an account by its ID
    public Account getAccountById(int accountId) {
        return accountRepository.getAccountById(accountId);
    }

    // Retrieves all accounts from the repository
    public List<Account> getAllAccounts() {
        return accountRepository.getAllAccounts();
    }

    // Validates account data such as username, password, fullname, email, and date
    // of birth
    public void validateAccountData(String username, String password, String fullName, String email, Date dob)
            throws Exception {
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username cannot be empty");
        }
        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            throw new Exception(
                    "Username must be between " + MIN_USERNAME_LENGTH + " and " + MAX_USERNAME_LENGTH + " characters");
        }
        if (!username.matches(USERNAME_REGEX)) {
            throw new Exception("Username must contain only letters and numbers");
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Password cannot be empty");
        }
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new Exception(
                    "Password must be between " + MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + " characters");
        }
        if (!password.matches(PASSWORD_REGEX)) {
            throw new Exception("Password must contain uppercase, lowercase, and a number");
        }

        // Validate fullname
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new Exception("Fullname cannot be empty");
        }
        if (!fullName.matches(FULLNAME_REGEX)) {
            throw new Exception("Fullname cannot contain special characters");
        }

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email cannot be empty");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new Exception("Invalid email format");
        }

        // Validate date of birth
        if (dob == null || dob.after(new Date())) {
            throw new Exception("Date of Birth cannot be today or in the future");
        }
    }

    // Creates a new account after validating the provided data
    public Account createAccount(CreateAccountRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();
        AccountRole accountRole = request.getAccountRole();

        // Validate account data
        validateAccountData(username, password, fullName, email, dob);
        if (accountRole == null) {
            throw new Exception("Account role must be specified");
        }

        // Create account in the repository
        Account account = accountRepository.createAccount(username, password, fullName, email, dob, accountRole);
        if (account == null) {
            throw new Exception("Account creation failed");
        }
        return account;
    }

    // Updates an existing account with the provided data
    public Account updateAccount(int accountId, UpdateAccountRequest request) throws Exception {
        if (accountId <= 0) {
            throw new Exception("Invalid account ID");
        }

        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();
        AccountRole accountRole = request.getAccountRole();

        // Validate account data
        validateAccountData(username, password, fullName, email, dob);
        if (accountRole == null) {
            throw new Exception("Account role must be specified");
        }

        // Update account in the repository
        Account account = accountRepository.updateAccount(accountId, username, password, fullName, email, dob,
                accountRole);
        if (account == null) {
            throw new Exception("Account update failed");
        }
        return account;
    }

    // Deletes an account by its ID from the repository
    public void deleteAccountById(int accountId) {
        accountRepository.deleteAccountById(accountId);
    }
}
