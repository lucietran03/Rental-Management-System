package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;

import java.util.Date;

public class UpdateOwnerRequest extends UpdateAccountRequest {
    public UpdateOwnerRequest(String username, String password, String fullname, String email, Date dob) {
        super(username, password, fullname, email, dob, AccountRole.OWNER);
    }
}
