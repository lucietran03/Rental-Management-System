package com.trustmejunior.model.Property;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;

public class ResidentialProperty extends Property {
    private int numberOfBedrooms;
    private boolean hasGarden;
    private boolean isPetFriendly;

    public ResidentialProperty(int rPropertyId, double price, String address, PropertyStatus status, int ownerId,
            int numberOfBedrooms, boolean hasGarden, boolean isPetFriendly) {
        super(rPropertyId, price, address, status, PropertyType.RESIDENTIAL_PROPERTY, ownerId);
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
