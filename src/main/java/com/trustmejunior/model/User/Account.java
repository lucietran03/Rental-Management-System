package com.trustmejunior.model.User;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;

import java.util.Date;

public class Account {
    protected int accountId;
    protected String username;
    protected String password;
    protected String fullName;
    protected String email;
    protected Date dob;
    protected AccountRole role;
    public Account() {}

    public Account(int accountId, String username, String password, String fullName, String email, Date dob,
            AccountRole role) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.dob = dob;
        this.role = role;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Date getDob() {
        return dob;
    }

    public AccountRole getAccountRole() {
        return role;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public void setAccountRole(AccountRole role) {
        this.role = role;
    }
}
