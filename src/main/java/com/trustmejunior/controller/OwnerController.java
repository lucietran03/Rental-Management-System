package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.User.Owner;
import com.trustmejunior.request.CreateOwnerRequest;
import com.trustmejunior.request.UpdateOwnerRequest;
import com.trustmejunior.service.OwnerService;

import java.util.List;

public class OwnerController {
    private OwnerService ownerService;

    public OwnerController() {
        this.ownerService = new OwnerService();
    }

    public Owner getOwnerByAccountId(int accountId) {
        return ownerService.getOwnerByAccountId(accountId);
    }

    public List<Owner> getAllOwners() {
        return ownerService.getAllOwners();
    }

    public Owner createOwner(CreateOwnerRequest request) throws Exception {
        return ownerService.createOwner(request);
    }

    public Owner updateOwner(int ownerId, UpdateOwnerRequest request) throws Exception {
        return ownerService.updateOwner(ownerId, request);
    }

    public void deleteOwnerByAccountId(int ownerId) {
        ownerService.deleteOwnerByAccountId(ownerId);
    }
}
