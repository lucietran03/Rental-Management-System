package com.trustmejunior.model.Property;

/**
 * @author TrustMeJunior
 */

public class PropertyImage {
    private int imageId;
    private String url;
    private int propertyId;

    public PropertyImage(int imageId, String url, int propertyId) {
        this.imageId = imageId;
        this.url = url;
        this.propertyId = propertyId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getUrl() {
        return url;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }
}
