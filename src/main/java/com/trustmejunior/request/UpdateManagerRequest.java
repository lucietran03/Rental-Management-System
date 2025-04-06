package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;

import java.util.Date;

public class UpdateManagerRequest extends UpdateAccountRequest {
    public UpdateManagerRequest(String username, String password, String email, String fullName, Date dob) {
        super(username, password, email, fullName, dob, AccountRole.MANAGER);
    }
}
