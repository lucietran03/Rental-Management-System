package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;

import java.util.Date;

public class UpdateTenantRequest extends UpdateAccountRequest {
    public UpdateTenantRequest(String username, String password, String fullName, String email, Date dob) {
        super(username, password, fullName, email, dob, AccountRole.TENANT);
    }
}
