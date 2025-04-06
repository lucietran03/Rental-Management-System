package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.User.Account;
import com.trustmejunior.request.CreateAccountRequest;
import com.trustmejunior.request.UpdateAccountRequest;
import com.trustmejunior.request.LoginRequest;
import com.trustmejunior.service.AccountService;

import java.util.List;

public class AccountController {
    private AccountService accountService;

    public AccountController() {
        this.accountService = new AccountService();
    }

    public Account login(LoginRequest request) {
        return accountService.login(request);
    }

    public Account getAccountById(int accountId) {
        return accountService.getAccountById(accountId);
    }

    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    public Account createAccount(CreateAccountRequest request) throws Exception {
        return accountService.createAccount(request);
    }

    public Account updateAccount(int accountId, UpdateAccountRequest request) throws Exception {
        return accountService.updateAccount(accountId, request);
    }

    public void deleteAccountById(int accountId) {
        accountService.deleteAccountById(accountId);
    }
}
