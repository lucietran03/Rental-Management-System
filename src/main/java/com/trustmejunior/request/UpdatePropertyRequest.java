package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;

public class UpdatePropertyRequest {
    private double price;
    private String address;
    private PropertyStatus status;
    private PropertyType type;
    private int ownerId;

    public UpdatePropertyRequest(double price, String address, PropertyStatus status, PropertyType type,
            int ownerId) {
        this.price = price;
        this.address = address;
        this.status = status;
        this.type = type;
        this.ownerId = ownerId;
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
