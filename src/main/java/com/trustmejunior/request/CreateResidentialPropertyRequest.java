package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;

public class CreateResidentialPropertyRequest extends CreatePropertyRequest {
    private int numberOfBedrooms;
    private boolean hasGarden;
    private boolean isPetFriendly;

    public CreateResidentialPropertyRequest(double price, String address, PropertyStatus status, int ownerId,
            int numberOfBedrooms, boolean hasGarden, boolean isPetFriendly) {
        super(price, address, status, PropertyType.RESIDENTIAL_PROPERTY, ownerId);
        this.numberOfBedrooms = numberOfBedrooms;
        this.hasGarden = hasGarden;
        this.isPetFriendly = isPetFriendly;
    }

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public boolean isHasGarden() {
        return hasGarden;
    }

    public boolean isPetFriendly() {
        return isPetFriendly;
    }

    public void setNumberOfBedrooms(int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public void setHasGarden(boolean hasGarden) {
        this.hasGarden = hasGarden;
    }

    public void setPetFriendly(boolean petFriendly) {
        isPetFriendly = petFriendly;
    }
}
