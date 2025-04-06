package com.trustmejunior.model.User;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;

import java.util.Date;

public class Manager extends Account {
    public Manager(int managerId, String username, String password, String fullName, String email, Date dob) {
        super(managerId, username, password, fullName, email, dob, AccountRole.MANAGER);
    }
}
