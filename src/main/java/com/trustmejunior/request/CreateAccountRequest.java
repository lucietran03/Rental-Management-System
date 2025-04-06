package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import java.util.Date;

import com.trustmejunior.model.Enum.AccountRole;

public class CreateAccountRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Date dob;
    private AccountRole role;

    public CreateAccountRequest(String username, String password, String fullName, String email, Date dob,
            AccountRole accountRole) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.dob = dob;
        this.role = accountRole;
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
