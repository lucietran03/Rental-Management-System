package com.trustmejunior.service;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.RentalPeriod;
import com.trustmejunior.model.Enum.RentalStatus;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Host;
import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.repository.*;
import com.trustmejunior.request.CreateRentalAgreementRequest;
import com.trustmejunior.request.UpdateRentalAgreementRequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RentalAgreementService {

    private HostRentalAgreementRepository hostRentalAgreementRepository;
    private HostRepository hostRepository;
    private RentalAgreementRepository rentalAgreementRepository;
    private TenantRentalAgreementRepository tenantRentalAgreementRepository;
    private TenantRepository tenantRepository;

    public RentalAgreementService() {
        this.hostRentalAgreementRepository = new HostRentalAgreementRepository();
        this.hostRepository = new HostRepository();
        this.rentalAgreementRepository = new RentalAgreementRepository();
        this.tenantRentalAgreementRepository = new TenantRentalAgreementRepository();
        this.tenantRepository = new TenantRepository();
    }

    public RentalAgreement getRentalAgreementById(int id) {
        return rentalAgreementRepository.getRentalAgreementById(id);
    }

    public List<RentalAgreement> getAllRentalAgreements() {
        return rentalAgreementRepository.getAllRentalAgreements();
    }

    public List<RentalAgreement> getRentalAgreementsByHostId(int id) {
        return rentalAgreementRepository.getRentalAgreementsByHostId(id);
    }

    public List<RentalAgreement> getRentalAgreementsByOwnerId(int ownerId) {
        return rentalAgreementRepository.getRentalAgreementsByOwnerId(ownerId);
    }

    public List<RentalAgreement> getRentalAgreementsByPropertyId(int id) {
        return rentalAgreementRepository.getRentalAgreementsByPropertyId(id);
    }

    public List<RentalAgreement> getRentalAgreementsByMainTenantId(int tenantId) {
        return rentalAgreementRepository.getRentalAgreementsByMainTenantId(tenantId);
    }

    public List<RentalAgreement> getRentalAgreementsBySubTenantId(int id) {
        return rentalAgreementRepository.getRentalAgreementsBySubTenantId(id);
    }

    // Validates rental agreement data by checking fee, dates, rental period,
    // status, and associated IDs.
    private void validateRentalAgreementData(double fee, Date startDate, Date endDate, RentalPeriod period,
            RentalStatus status, int ownerId, int propertyId) throws Exception {
        // Ensure rental fee is positive.
        if (fee <= 0)
            throw new Exception("Rental fee must be greater than 0");

        // Validate start and end dates.
        if (startDate == null)
            throw new Exception("Start date cannot be empty");
        if (endDate == null)
            throw new Exception("End date cannot be empty");
        if (startDate.after(endDate))
            throw new Exception("Start date cannot be after end date");

        // Ensure period, status, and IDs are valid.
        if (period == null)
            throw new Exception("Rental period cannot be empty");
        if (status == null)
            throw new Exception("Rental status cannot be empty");
        if (ownerId <= 0)
            throw new Exception("Invalid owner ID");
        if (propertyId <= 0)
            throw new Exception("Invalid property ID");

        // Calculate rental period in months and validate based on the rental period
        // type.
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long months = ChronoUnit.MONTHS.between(startLocalDate, endLocalDate);

        switch (period) {
            case WEEKLY -> { // Weekly rental must be 1-3 months.
                if (months < 1 || months > 3)
                    throw new Exception("Rental period must be between 1 and 3 months");
            }
            case MONTHLY -> { // Monthly rental must be 3-12 months.
                if (months < 3 || months > 12)
                    throw new Exception("Rental period must be between 3 and 12 months");
            }
            case YEARLY -> { // Yearly rental must be 1-10 years.
                if (months < 12 || months > 120)
                    throw new Exception("Rental period must be between 1 year to 10 years");
            }
        }
    }

    // Creates a new rental agreement after validating input data.
    public RentalAgreement createRentalAgreement(CreateRentalAgreementRequest request) throws Exception {
        // Extract and validate input data.
        double fee = request.getFee();
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        RentalPeriod period = request.getPeriod();
        RentalStatus status = request.getStatus();
        int ownerId = request.getOwnerId();
        int propertyId = request.getPropertyId();
        validateRentalAgreementData(fee, startDate, endDate, period, status, ownerId, propertyId);

        // Create rental agreement and handle errors if creation fails.
        RentalAgreement rentalAgreement = rentalAgreementRepository.createRentalAgreement(fee, startDate, endDate,
                period, status, ownerId, propertyId);
        if (rentalAgreement == null)
            throw new Exception("Failed to create rental agreement.");
        return rentalAgreement;
    }

    // Updates an existing rental agreement after validating input data.
    public RentalAgreement updateRentalAgreement(int rentalAgreementId, UpdateRentalAgreementRequest request)
            throws Exception {
        if (rentalAgreementId <= 0)
            throw new Exception("Invalid rental agreement ID");

        // Extract and validate input data.
        double fee = request.getFee();
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        RentalPeriod period = request.getPeriod();
        RentalStatus status = request.getStatus();
        int ownerId = request.getOwnerId();
        int propertyId = request.getPropertyId();
        validateRentalAgreementData(fee, startDate, endDate, period, status, ownerId, propertyId);

        // Update rental agreement and handle errors if update fails.
        RentalAgreement rentalAgreement = rentalAgreementRepository.updateRentalAgreement(rentalAgreementId, fee,
                startDate, endDate, period,
                status, ownerId, propertyId);
        if (rentalAgreement == null)
            throw new Exception("Failed to update rental agreement");
        return rentalAgreement;
    }

    // Deletes a rental agreement by ID.
    public void deleteRentalAgreementById(int rentalAgreementId) {
        rentalAgreementRepository.deleteRentalAgreementById(rentalAgreementId);
    }

    // Updates host associations for a rental agreement.
    public void setHostIds(int rentalAgreementId, List<Integer> hostIds) {
        List<Host> oldHosts = hostRepository.getHostsByRentalAgreementId(rentalAgreementId);
        List<Integer> oldHostIds = oldHosts.stream().map(Host::getAccountId).toList();

        // Link new hosts.
        for (int hostId : hostIds) {
            if (!oldHostIds.contains(hostId)) {
                hostRentalAgreementRepository.linkHostToRentalAgreement(hostId, rentalAgreementId);
            }
        }

        // Unlink removed hosts.
        for (int oldHostId : oldHostIds) {
            if (!hostIds.contains(oldHostId)) {
                hostRentalAgreementRepository.unlinkHostFromRentalAgreement(oldHostId, rentalAgreementId);
            }
        }
    }

    // Assigns a main tenant to a rental agreement.
    public void setMainTenantId(int rentalAgreementId, int newTenantId) {
        // Get current main tenant
        Tenant currentMainTenant = tenantRepository.getMainTenantByRentalAgreementId(rentalAgreementId);

        // If there is an existing main tenant, unlink them first
        if (currentMainTenant != null) {
            tenantRentalAgreementRepository.unlinkTenantFromRentalAgreement(
                    currentMainTenant.getAccountId(),
                    rentalAgreementId,
                    "MAIN");
        }

        // Link the new main tenant
        tenantRentalAgreementRepository.linkTenantToRentalAgreement(
                newTenantId,
                rentalAgreementId,
                "MAIN");
    }

    // Updates sub-tenant associations for a rental agreement.
    public void setSubTenantIds(int rentalAgreementId, List<Integer> tenantIds) {
        List<Tenant> oldTenants = tenantRepository.getSubTenantsByRentalAgreementId(rentalAgreementId);
        List<Integer> oldTenantIds = oldTenants.stream().map(Tenant::getAccountId).toList();

        // Link new sub-tenants.
        for (int tenantId : tenantIds) {
            if (!oldTenantIds.contains(tenantId)) {
                tenantRentalAgreementRepository.linkTenantToRentalAgreement(tenantId, rentalAgreementId, "SUB");
            }
        }

        // Unlink removed sub-tenants.
        for (int oldTenantId : oldTenantIds) {
            if (!tenantIds.contains(oldTenantId)) {
                tenantRentalAgreementRepository.unlinkTenantFromRentalAgreement(oldTenantId, rentalAgreementId, "SUB");
            }
        }
    }

    public int getTotalRentalAgreements(Integer hostId) {
        return rentalAgreementRepository.getTotalRentalAgreements(hostId);
    }

    public double getTotalRevenue(Integer hostId) {
        return rentalAgreementRepository.getTotalRevenue(hostId);
    }

    public Map<String, Map<String, Double>> getMonthlyRevenueTrend(String selectedYear, Integer hostId) {
        return rentalAgreementRepository.getMonthlyRevenueTrend(selectedYear, hostId);
    }

    public List<String> getAvailableYears() {
        return rentalAgreementRepository.getAvailableYears();
    }

    public Map<String, Map<String, Double>> getYearlyRevenueTrend(Integer hostId) {
        return rentalAgreementRepository.getYearlyRevenueTrend(hostId);
    }

    public Map<String, Double> getTop5PropertiesByRevenue(Integer hostId) {
        return rentalAgreementRepository.getTop5PropertiesByRevenue(hostId);
    }

    public Map<Integer, Integer> getTop10OverduePaymentsByRA(Integer hostId) {
        return rentalAgreementRepository.getTop10OverduePaymentsByRA(hostId);
    }

    public Map<String, Integer> getVacantVsRentedProperties(Integer hostId) {
        return rentalAgreementRepository.getVacantVsRentedProperties(hostId);
    }

    public List<String> getYears() {
        return rentalAgreementRepository.getYears();
    }

    public Map<Integer, Integer> getRentalCountsForAllYears(Integer hostId) {
        return rentalAgreementRepository.getRentalCountsForAllYears(hostId);
    }

    public Map<Integer, Integer> getRentalCountsByMonth(int year, Integer hostId) {
        return rentalAgreementRepository.getRentalCountsByMonth(year, hostId);
    }
}
