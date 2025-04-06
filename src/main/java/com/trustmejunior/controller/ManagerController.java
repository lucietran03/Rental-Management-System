package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import java.util.List;

import com.trustmejunior.model.User.Manager;
import com.trustmejunior.request.CreateManagerRequest;
import com.trustmejunior.request.UpdateManagerRequest;
import com.trustmejunior.service.ManagerService;

public class ManagerController {
    private ManagerService managerService;

    public ManagerController() {
        this.managerService = new ManagerService();
    }

    public Manager getManagerByAccountId(int accountId) {
        return managerService.getManagerByAccountId(accountId);
    }

    public List<Manager> getAllManagers() {
        return managerService.getAllManagers();
    }

    public Manager createManager(CreateManagerRequest request) throws Exception {
        return managerService.createManager(request);
    }

    public Manager updateManager(int managerId, UpdateManagerRequest request) throws Exception {
        return managerService.updateManager(managerId, request);
    }

    public void deleteManagerByAccountId(int managerId) {
        managerService.deleteManagerByAccountId(managerId);
    }
}
