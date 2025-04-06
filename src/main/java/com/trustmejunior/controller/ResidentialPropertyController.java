package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.service.ResidentialPropertyService;
import com.trustmejunior.model.Property.ResidentialProperty;
import com.trustmejunior.request.CreateResidentialPropertyRequest;
import com.trustmejunior.request.UpdateResidentialPropertyRequest;

import java.util.List;

public class ResidentialPropertyController {
    private ResidentialPropertyService residentialPropertyService;

    public ResidentialPropertyController() {
        this.residentialPropertyService = new ResidentialPropertyService();
    }

    public ResidentialProperty getResidentialPropertyById(int propertyId) {
        return residentialPropertyService.getResidentialPropertyById(propertyId);
    }

    public List<ResidentialProperty> getAllResidentialProperties() {
        return residentialPropertyService.getAllResidentialProperties();
    }

    public ResidentialProperty createResidentialProperty(CreateResidentialPropertyRequest request) throws Exception {
        return residentialPropertyService.createResidentialProperty(request);
    }

    public ResidentialProperty updateResidentialProperty(int rPropertyId, UpdateResidentialPropertyRequest request)
            throws Exception {
        return residentialPropertyService.updateResidentialProperty(rPropertyId, request);
    }

    public void deleteResidentialPropertyById(int rPropertyId) {
        residentialPropertyService.deleteResidentialPropertyById(rPropertyId);
    }
}
