package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import java.util.List;

import com.trustmejunior.model.User.Tenant;
import com.trustmejunior.request.CreateTenantRequest;
import com.trustmejunior.request.UpdateTenantRequest;
import com.trustmejunior.service.TenantService;

public class TenantController {
    private TenantService tenantService;

    public TenantController() {
        this.tenantService = new TenantService();
    }

    public Tenant getTenantByAccountId(int accountId) {
        return tenantService.getTenantByAccountId(accountId);
    }

    public Tenant getMainTenantByRentalAgreementId(int id) {
        return tenantService.getMainTenantByRentalAgreementId(id);
    }

    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    public List<Tenant> getSubTenantsByRentalAgreementId(int id) {
        return tenantService.getSubTenantsByRentalAgreementId(id);
    }

    public Tenant createTenant(CreateTenantRequest request) throws Exception {
        return tenantService.createTenant(request);
    }

    public Tenant updateTenant(int ownerId, UpdateTenantRequest request) throws Exception {
        return tenantService.updateTenant(ownerId, request);
    }

    public void deleteTenantByAccountId(int ownerId) {
        tenantService.deleteTenantByAccountId(ownerId);
    }

    public void setMainRentalAgreementIds(int ownerId, List<Integer> rentalAgreementIds) {
        tenantService.setMainRentalAgreementIds(ownerId, rentalAgreementIds);
    }

    public void setSubRentalAgreementIds(int ownerId, List<Integer> rentalAgreementIds) {
        tenantService.setSubRentalAgreementIds(ownerId, rentalAgreementIds);
    }

    public int getTotalTenants() {
        return tenantService.getTotalTenants();
    }
}
