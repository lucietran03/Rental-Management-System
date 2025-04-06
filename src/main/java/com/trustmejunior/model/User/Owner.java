package com.trustmejunior.model.User;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;

import java.util.Date;

public class Owner extends Account {
    public Owner(int ownerId, String username, String password, String fullName, String email, Date dob) {
        super(ownerId, username, password, fullName, email, dob, AccountRole.OWNER);
    }
}
