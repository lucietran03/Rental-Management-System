package com.trustmejunior.model.Property;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyBusinessType;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;

public class CommercialProperty extends Property {
    private PropertyBusinessType businessType;
    private double area;
    private boolean hasParking;

    public CommercialProperty(int cPropertyId, double price, String address, PropertyStatus status, int ownerId,
            PropertyBusinessType businessType, double area, boolean hasParking) {
        super(cPropertyId, price, address, status, PropertyType.COMMERCIAL_PROPERTY, ownerId);
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
