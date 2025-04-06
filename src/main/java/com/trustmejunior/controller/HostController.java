package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import java.util.List;

import com.trustmejunior.model.User.Host;
import com.trustmejunior.request.CreateHostRequest;
import com.trustmejunior.request.UpdateHostRequest;
import com.trustmejunior.service.HostService;

public class HostController {
    private HostService hostService;

    public HostController() {
        this.hostService = new HostService();
    }

    public Host getHostByAccountId(int accountId) {
        return hostService.getHostByAccountId(accountId);
    }

    public List<Host> getAllHosts() {
        return hostService.getAllHosts();
    }

    public List<Host> getHostsByRentalAgreementId(int id) {
        return hostService.getHostsByRentalAgreementId(id);
    }

    public List<Host> getHostsByPropertyId(int id) {
        return hostService.getHostsByPropertyId(id);
    }

    public Host createHost(CreateHostRequest request) throws Exception {
        return hostService.createHost(request);
    }

    public Host updateHost(int hostId, UpdateHostRequest request) throws Exception {
        return hostService.updateHost(hostId, request);
    }

    public void deleteHostByAccountId(int hostId) {
        hostService.deleteHostByAccountId(hostId);
    }

    public void setPropertyIds(int hostId, List<Integer> propertyIds) {
        hostService.setPropertyIds(hostId, propertyIds);
    }

    public void setRentalAgreementIds(int hostId, List<Integer> rentalAgreementIds) {
        hostService.setRentalAgreementIds(hostId, rentalAgreementIds);
    }
}
