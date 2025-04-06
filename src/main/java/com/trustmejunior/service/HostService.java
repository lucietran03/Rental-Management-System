package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.Property.Property;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.repository.AccountRepository;
import com.trustmejunior.repository.HostPropertyRepository;
import com.trustmejunior.repository.HostRentalAgreementRepository;
import com.trustmejunior.repository.HostRepository;
import com.trustmejunior.repository.PropertyRepository;
import com.trustmejunior.repository.RentalAgreementRepository;
import com.trustmejunior.request.CreateHostRequest;
import com.trustmejunior.request.UpdateHostRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HostService {
    private HostRepository hostRepository;
    private HostPropertyRepository hostPropertyRepository;
    private HostRentalAgreementRepository hostRentalAgreementRepository;
    private AccountRepository accountRepository;
    private PropertyRepository propertyRepository;
    private RentalAgreementRepository rentalAgreementRepository;
    private AccountService accountService;

    public HostService() {
        this.hostRepository = new HostRepository();
        this.hostPropertyRepository = new HostPropertyRepository();
        this.hostRentalAgreementRepository = new HostRentalAgreementRepository();
        this.accountRepository = new AccountRepository();
        this.propertyRepository = new PropertyRepository();
        this.rentalAgreementRepository = new RentalAgreementRepository();
        this.accountService = new AccountService();
    }

    public Host getHostByAccountId(int accountId) {
        return hostRepository.getHostByAccountId(accountId);
    }

    public List<Host> getAllHosts() {
        return hostRepository.getAllHosts();
    }

    public List<Host> getHostsByRentalAgreementId(int id) {
        return hostRepository.getHostsByRentalAgreementId(id);
    }

    public List<Host> getHostsByPropertyId(int id) {
        return hostRepository.getHostsByPropertyId(id);
    }

    // Creates a new host by validating the account data and creating the associated
    // account and host
    public Host createHost(CreateHostRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();

        // Validate account data before creation
        accountService.validateAccountData(username, password, fullName, email, dob);

        // Create the account for the host
        Account account = accountRepository.createAccount(username, password, fullName, email, dob, AccountRole.HOST);

        if (account == null) {
            throw new Exception("Host creation failed");
        }

        int accountId = account.getAccountId();

        // Create the host using the account ID
        return hostRepository.createHost(accountId);
    }

    // Updates the host information, including account details
    public Host updateHost(int hostId, UpdateHostRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        Date dob = request.getDob();

        // Validate account data before updating
        accountService.validateAccountData(username, password, fullName, email, dob);

        // Update the host's account details
        Account account = accountRepository.updateAccount(hostId, username, password, fullName, email, dob,
                AccountRole.HOST);

        if (account == null) {
            throw new Exception("Host update failed");
        }

        // Return the updated host using the host ID
        return hostRepository.getHostByAccountId(hostId);
    }

    // Deletes the host by their account ID, removing both account and host data
    public void deleteHostByAccountId(int hostId) {
        accountRepository.deleteAccountById(hostId);
        hostRepository.deleteHostByAccountId(hostId);
    }

    // Links new properties to the host and unlinks old properties no longer
    // associated with the host
    public void setPropertyIds(int hostId, List<Integer> propertyIds) {
        List<Property> oldProperties = propertyRepository.getPropertiesByHostId(hostId);
        List<Integer> oldPropertyIds = new ArrayList<Integer>();

        // Collect old property IDs
        for (Property property : oldProperties) {
            oldPropertyIds.add(property.getPropertyId());
        }

        // Link new properties to the host
        for (int propertyId : propertyIds) {
            if (!oldPropertyIds.contains(propertyId)) {
                hostPropertyRepository.linkHostToProperty(hostId, propertyId);
            }
        }

        // Unlink properties that are no longer associated with the host
        for (int oldPropertyId : oldPropertyIds) {
            if (!propertyIds.contains(oldPropertyId)) {
                hostPropertyRepository.unlinkHostFromProperty(hostId, oldPropertyId);
            }
        }
    }

    // Links new rental agreements to the host and unlinks old rental agreements no
    // longer associated with the host
    public void setRentalAgreementIds(int hostId, List<Integer> rentalAgreementIds) {
        List<RentalAgreement> oldRentalAgreements = rentalAgreementRepository.getRentalAgreementsByHostId(hostId);
        List<Integer> oldRentalAgreementIds = new ArrayList<Integer>();

        // Collect old rental agreement IDs
        for (RentalAgreement rentalAgreement : oldRentalAgreements) {
            oldRentalAgreementIds.add(rentalAgreement.getRentalAgreementId());
        }

        // Link new rental agreements to the host
        for (int rentalAgreementId : rentalAgreementIds) {
            if (!oldRentalAgreementIds.contains(rentalAgreementId)) {
                hostRentalAgreementRepository.linkHostToRentalAgreement(hostId, rentalAgreementId);
            }
        }

        // Unlink rental agreements that are no longer associated with the host
        for (int oldRentalAgreementId : oldRentalAgreementIds) {
            if (!rentalAgreementIds.contains(oldRentalAgreementId)) {
                hostRentalAgreementRepository.unlinkHostFromRentalAgreement(hostId, oldRentalAgreementId);
            }
        }
    }
}
