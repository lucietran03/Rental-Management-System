package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.repository.TenantRentalAgreementRepository;
import com.trustmejunior.repository.TenantRepository;
import com.trustmejunior.repository.AccountRepository;
import com.trustmejunior.repository.RentalAgreementRepository;
import com.trustmejunior.request.CreateTenantRequest;
import com.trustmejunior.request.UpdateTenantRequest;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TenantService {
    private TenantRepository tenantRepository;
    private TenantRentalAgreementRepository tenantRentalAgreementRepository;
    private AccountRepository accountRepository;
    private RentalAgreementRepository rentalAgreementRepository;
    private AccountService accountService;

    public TenantService() {
        this.tenantRepository = new TenantRepository();
        this.tenantRentalAgreementRepository = new TenantRentalAgreementRepository();
        this.accountRepository = new AccountRepository();
        this.rentalAgreementRepository = new RentalAgreementRepository();
        this.accountService = new AccountService();
    }

    public Tenant getTenantByAccountId(int accountId) {
        return tenantRepository.getTenantByAccountId(accountId);
    }

    public Tenant getMainTenantByRentalAgreementId(int id) {
        return tenantRepository.getMainTenantByRentalAgreementId(id);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.getAllTenants();
    }

    public List<Tenant> getSubTenantsByRentalAgreementId(int id) {
        return tenantRepository.getSubTenantsByRentalAgreementId(id);
    }

    // Creates a new tenant by validating account data and linking the tenant to the
    // account.
    public Tenant createTenant(CreateTenantRequest request) throws Exception {
        // Retrieve tenant details from the request
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();

        // Validate account data
        accountService.validateAccountData(username, password, fullName, email, dob);

        // Create the account and check for success
        Account account = accountRepository.createAccount(username, password, fullName, email, dob, AccountRole.TENANT);
        if (account == null) {
            throw new Exception("Tenant creation failed");
        }

        // Create and return the tenant
        int accountId = account.getAccountId();
        return tenantRepository.createTenant(accountId);
    }

    // Updates tenant information by validating and modifying account data.
    public Tenant updateTenant(int tenantId, UpdateTenantRequest request) throws Exception {
        // Retrieve updated tenant details from the request
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();

        // Validate account data
        accountService.validateAccountData(username, password, fullName, email, dob);

        // Update the account and check for success
        Account account = accountRepository.updateAccount(tenantId, username, password, fullName, email, dob,
                AccountRole.TENANT);
        if (account == null) {
            throw new Exception("Tenant update failed");
        }

        // Retrieve and return the updated tenant
        return tenantRepository.getTenantByAccountId(tenantId);
    }

    // Deletes a tenant and associated account by tenant ID.
    public void deleteTenantByAccountId(int tenantId) {
        // Delete the associated tenant and account
        accountRepository.deleteAccountById(tenantId);
        tenantRepository.deleteTenantByAccountId(tenantId);
    }

    // Sets or updates the main rental agreements associated with a tenant.
    public void setMainRentalAgreementIds(int tenantId, List<Integer> rentalAgreementIds) {
        // Retrieve the tenant's current main rental agreements
        List<RentalAgreement> oldRentalAgreements = rentalAgreementRepository
                .getRentalAgreementsBySubTenantId(tenantId);
        List<Integer> oldRentalAgreementIds = oldRentalAgreements.stream()
                .map(RentalAgreement::getRentalAgreementId)
                .collect(Collectors.toList());

        // Link new rental agreements that aren't already associated
        for (int rentalAgreementId : rentalAgreementIds) {
            if (!oldRentalAgreementIds.contains(rentalAgreementId)) {
                tenantRentalAgreementRepository.linkTenantToRentalAgreement(tenantId, rentalAgreementId, "MAIN");
            }
        }

        // Unlink rental agreements that are no longer associated
        for (int oldRentalAgreementId : oldRentalAgreementIds) {
            if (!rentalAgreementIds.contains(oldRentalAgreementId)) {
                tenantRentalAgreementRepository.unlinkTenantFromRentalAgreement(tenantId, oldRentalAgreementId, "MAIN");
            }
        }
    }

    // Sets or updates the sub rental agreements associated with a tenant.
    public void setSubRentalAgreementIds(int tenantId, List<Integer> rentalAgreementIds) {
        // Retrieve the tenant's current sub rental agreements
        List<Integer> oldRentalAgreementIds = rentalAgreementRepository.getRentalAgreementsBySubTenantId(tenantId)
                .stream()
                .map(RentalAgreement::getRentalAgreementId)
                .collect(Collectors.toList());

        // Link new rental agreements that aren't already associated
        for (int rentalAgreementId : rentalAgreementIds) {
            if (!oldRentalAgreementIds.contains(rentalAgreementId)) {
                tenantRentalAgreementRepository.linkTenantToRentalAgreement(tenantId, rentalAgreementId, "SUB");
            }
        }

        // Unlink rental agreements that are no longer associated
        for (int oldRentalAgreementId : oldRentalAgreementIds) {
            if (!rentalAgreementIds.contains(oldRentalAgreementId)) {
                tenantRentalAgreementRepository.unlinkTenantFromRentalAgreement(tenantId, oldRentalAgreementId, "SUB");
            }
        }
    }

    public int getTotalTenants() {
        return tenantRepository.getTotalTenants();
    }
}
