package com.trustmejunior.model.Property;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;

public class Property {
    protected int propertyId;
    protected double price;
    protected String address;
    protected PropertyStatus status;
    protected PropertyType type;
    protected int ownerId;

    public Property(int propertyId, double price, String address, PropertyStatus status, PropertyType type,
            int ownerId) {
        this.propertyId = propertyId;
        this.price = price;
        this.address = address;
        this.status = status;
        this.type = type;
        this.ownerId = ownerId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public double getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public PropertyType getType() {
        return type;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }
}
