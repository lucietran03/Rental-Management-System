package com.trustmejunior.model.User;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;

import java.util.Date;

public class Host extends Account {
    public Host(int hostId, String username, String password, String fullName, String email, Date dob) {
        super(hostId, username, password, fullName, email, dob, AccountRole.HOST);
    }
}
