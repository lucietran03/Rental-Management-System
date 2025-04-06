package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Manager;
import com.trustmejunior.repository.ManagerRepository;
import com.trustmejunior.repository.AccountRepository;
import com.trustmejunior.request.CreateManagerRequest;
import com.trustmejunior.request.UpdateManagerRequest;

import java.util.Date;
import java.util.List;

public class ManagerService {
    private ManagerRepository managerRepository;
    private AccountRepository accountRepository;
    private AccountService accountService;

    public ManagerService() {
        this.managerRepository = new ManagerRepository();
        this.accountRepository = new AccountRepository();
        this.accountService = new AccountService();
    }

    public Manager getManagerByAccountId(int accountId) {
        return managerRepository.getManagerByAccountId(accountId);
    }

    public List<Manager> getAllManagers() {
        return managerRepository.getAllManagers();
    }

    // Creates a new manager by validating the account data and creating the
    // associated account and manager
    public Manager createManager(CreateManagerRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();

        // Validate account data before creating the manager
        accountService.validateAccountData(username, password, fullName, email, dob);

        // Create the manager's account
        Account account = accountRepository.createAccount(username, password, fullName, email, dob,
                AccountRole.MANAGER);

        if (account == null) {
            throw new Exception("Manager creation failed");
        }

        int accountId = account.getAccountId();

        // Create the manager using the account ID
        return managerRepository.createManager(accountId);
    }

    // Updates the manager's information, including account details
    public Manager updateManager(int managerId, UpdateManagerRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();

        // Validate account data before updating the manager's account
        accountService.validateAccountData(username, password, fullName, email, dob);

        // Update the manager's account details
        Account account = accountRepository.updateAccount(managerId, username, password, fullName, email, dob,
                AccountRole.MANAGER);

        if (account == null) {
            throw new Exception("Manager update failed");
        }

        // Return the updated manager using the manager ID
        return managerRepository.getManagerByAccountId(managerId);
    }

    // Deletes the manager by their account ID, removing both account and manager
    // data
    public void deleteManagerByAccountId(int managerId) {
        accountRepository.deleteAccountById(managerId);
        managerRepository.deleteManagerByAccountId(managerId);
    }
}
