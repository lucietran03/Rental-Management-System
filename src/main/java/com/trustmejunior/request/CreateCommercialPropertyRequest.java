package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.model.Enum.PropertyBusinessType;

public class CreateCommercialPropertyRequest extends CreatePropertyRequest {
    private PropertyBusinessType businessType;
    private double area;
    private boolean hasParking;

    public CreateCommercialPropertyRequest(double price, String address, PropertyStatus status, int ownerId,
            PropertyBusinessType businessType, double area, boolean hasParking) {
        super(price, address, status, PropertyType.COMMERCIAL_PROPERTY, ownerId);
        this.businessType = businessType;
        this.area = area;
        this.hasParking = hasParking;
    }

    public PropertyBusinessType getBusinessType() {
        return businessType;
    }

    public double getArea() {
        return area;
    }

    public boolean isHasParking() {
        return hasParking;
    }

    public void setBusinessType(PropertyBusinessType businessType) {
        this.businessType = businessType;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setHasParking(boolean hasParking) {
        this.hasParking = hasParking;
    }
}
