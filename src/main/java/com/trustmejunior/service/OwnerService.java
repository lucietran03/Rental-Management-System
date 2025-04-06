package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Owner;
import com.trustmejunior.repository.OwnerRepository;
import com.trustmejunior.repository.AccountRepository;
import com.trustmejunior.request.CreateOwnerRequest;
import com.trustmejunior.request.UpdateOwnerRequest;

import java.util.Date;
import java.util.List;

public class OwnerService {
    private OwnerRepository ownerRepository;
    private AccountRepository accountRepository;
    private AccountService accountService;

    public OwnerService() {
        this.ownerRepository = new OwnerRepository();
        this.accountRepository = new AccountRepository();
        this.accountService = new AccountService();
    }

    public Owner getOwnerByAccountId(int accountId) {
        return ownerRepository.getOwnerByAccountId(accountId);
    }

    public List<Owner> getAllOwners() {
        return ownerRepository.getAllOwners();
    }

    // Creates a new owner by validating input data and saving it in the database
    public Owner createOwner(CreateOwnerRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();

        // Validate account data
        accountService.validateAccountData(username, password, fullName, email, dob);

        // Create a new account with the role of OWNER
        Account account = accountRepository.createAccount(username, password, fullName, email, dob, AccountRole.OWNER);

        // Throw an exception if account creation fails
        if (account == null) {
            throw new Exception("owner creation failed");
        }

        // Create and return the owner entity associated with the account
        int accountId = account.getAccountId();
        return ownerRepository.createOwner(accountId);
    }

    // Updates an existing owner by validating new input data and updating the
    // database
    public Owner updateOwner(int ownerId, UpdateOwnerRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();

        // Validate new account data
        accountService.validateAccountData(username, password, fullName, email, dob);

        // Update the account with the role of OWNER
        Account account = accountRepository.updateAccount(ownerId, username, password, fullName, email, dob,
                AccountRole.OWNER);

        // Throw an exception if account update fails
        if (account == null) {
            throw new Exception("Owner update failed");
        }

        // Retrieve and return the updated owner entity
        return ownerRepository.getOwnerByAccountId(ownerId);
    }

    // Deletes an owner and its associated account from the database
    public void deleteOwnerByAccountId(int ownerId) {
        accountRepository.deleteAccountById(ownerId); // Delete the account
        ownerRepository.deleteOwnerByAccountId(ownerId); // Delete the owner entity
    }
}
