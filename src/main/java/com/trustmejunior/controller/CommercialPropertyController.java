package com.trustmejunior.controller;

/**
 * @author TrustMeJunior
 */

import java.util.List;

import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.request.CreateCommercialPropertyRequest;
import com.trustmejunior.request.UpdateCommercialPropertyRequest;
import com.trustmejunior.service.CommercialPropertyService;

public class CommercialPropertyController {
    private CommercialPropertyService commercialPropertyService;

    public CommercialPropertyController() {
        this.commercialPropertyService = new CommercialPropertyService();
    }

    public CommercialProperty getCommercialPropertyById(int propertyId) {
        return commercialPropertyService.getCommercialPropertyById(propertyId);
    }

    public List<CommercialProperty> getAllCommercialProperties() {
        return commercialPropertyService.getAllCommercialProperties();
    }

    public CommercialProperty createCommercialProperty(CreateCommercialPropertyRequest request) throws Exception {
        return commercialPropertyService.createCommercialProperty(request);
    }

    public CommercialProperty updateCommercialProperty(int cPropertyId, UpdateCommercialPropertyRequest request)
            throws Exception {
        return commercialPropertyService.updateCommercialProperty(cPropertyId, request);
    }

    public void deleteCommercialPropertyById(int cPropertyId) {
        commercialPropertyService.deleteCommercialPropertyById(cPropertyId);
    }
}
