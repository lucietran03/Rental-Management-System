package com.trustmejunior.request;

/**
 * @author TrustMeJunior
 */

public class CreatePropertyImageRequest {
    private String url;
    private int propertyId;

    public CreatePropertyImageRequest(String url, int propertyId) {
        this.url = url;
        this.propertyId = propertyId;
    }

    public String getUrl() {
        return url;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }
}
