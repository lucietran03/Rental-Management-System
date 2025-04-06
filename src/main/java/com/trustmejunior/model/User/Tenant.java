package com.trustmejunior.model.User;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;

import java.util.Date;

public class Tenant extends Account {
    public Tenant(int tenantId, String username, String password, String fullName, String email, Date dob) {
        super(tenantId, username, password, fullName, email, dob, AccountRole.TENANT);
    }
}
