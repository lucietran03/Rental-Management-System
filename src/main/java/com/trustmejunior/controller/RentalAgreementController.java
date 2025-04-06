package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.request.CreateRentalAgreementRequest;
import com.trustmejunior.request.UpdateRentalAgreementRequest;
import com.trustmejunior.service.RentalAgreementService;

import java.util.List;
import java.util.Map;

public class RentalAgreementController {
    private RentalAgreementService rentalAgreementService;

    public RentalAgreementController() {
        this.rentalAgreementService = new RentalAgreementService();
    }

    public List<RentalAgreement> getAllRentalAgreements() {
        return rentalAgreementService.getAllRentalAgreements();
    }

    public RentalAgreement getRentalAgreementById(int agreementId) {
        return rentalAgreementService.getRentalAgreementById(agreementId);
    }

    public List<RentalAgreement> getRentalAgreementsByHostId(int hostId) {
        return rentalAgreementService.getRentalAgreementsByHostId(hostId);
    }

    public List<RentalAgreement> getRentalAgreementsByOwnerId(int ownerId) {
        return rentalAgreementService.getRentalAgreementsByOwnerId(ownerId);
    }

    public List<RentalAgreement> getRentalAgreementsByPropertyId(int propertyId) {
        return rentalAgreementService.getRentalAgreementsByPropertyId(propertyId);
    }

    public List<RentalAgreement> getRentalAgreementsByMainTenantId(int tenantId) {
        return rentalAgreementService.getRentalAgreementsByMainTenantId(tenantId);
    }

    public List<RentalAgreement> getRentalAgreementsBySubTenantId(int tenantId) {
        return rentalAgreementService.getRentalAgreementsBySubTenantId(tenantId);
    }

    public RentalAgreement createRentalAgreement(CreateRentalAgreementRequest request) throws Exception {
        return rentalAgreementService.createRentalAgreement(request);
    }

    public RentalAgreement updateRentalAgreement(int agreementId, UpdateRentalAgreementRequest request)
            throws Exception {
        return rentalAgreementService.updateRentalAgreement(agreementId, request);
    }

    public void deleteRentalAgreementById(int agreementId) {
        rentalAgreementService.deleteRentalAgreementById(agreementId);
    }

    public void setHostIds(int agreementId, List<Integer> hostIds) {
        rentalAgreementService.setHostIds(agreementId, hostIds);
    }

    public void setMainTenantId(int agreementId, int tenantId) {
        rentalAgreementService.setMainTenantId(agreementId, tenantId);
    }

    public void setSubTenantIds(int agreementId, List<Integer> tenantIds) {
        rentalAgreementService.setSubTenantIds(agreementId, tenantIds);
    }

    public int getTotalRentalAgreements(Integer hostId) {
        return rentalAgreementService.getTotalRentalAgreements(hostId);
    }

    public double getTotalRevenue(Integer hostId) {
        return rentalAgreementService.getTotalRevenue(hostId);
    }

    public Map<String, Map<String, Double>> getMonthlyRevenueTrend(String selectedYear, Integer hostId) {
        return rentalAgreementService.getMonthlyRevenueTrend(selectedYear, hostId);
    }

    public List<String> getAvailableYears() {
        return rentalAgreementService.getAvailableYears();
    }

    public Map<String, Map<String, Double>> getYearlyRevenueTrend(Integer hostId) {
        return rentalAgreementService.getYearlyRevenueTrend(hostId);
    }

    public Map<String, Double> getTop5PropertiesByRevenue(Integer hostId) {
        return rentalAgreementService.getTop5PropertiesByRevenue(hostId);
    }

    public Map<Integer, Integer> getTop10OverduePaymentsByRA(Integer hostId) {
        return rentalAgreementService.getTop10OverduePaymentsByRA(hostId);
    }

    public Map<String, Integer> getVacantVsRentedProperties(Integer hostId) {
        return rentalAgreementService.getVacantVsRentedProperties(hostId);
    }

    public List<String> getYears() {
        return rentalAgreementService.getYears();
    }

    public Map<Integer, Integer> getRentalCountsForAllYears(Integer hostId) {
        return rentalAgreementService.getRentalCountsForAllYears(hostId);
    }

    public Map<Integer, Integer> getRentalCountsByMonth(int year, Integer hostId) {
        return rentalAgreementService.getRentalCountsByMonth(year, hostId);
    }
}
