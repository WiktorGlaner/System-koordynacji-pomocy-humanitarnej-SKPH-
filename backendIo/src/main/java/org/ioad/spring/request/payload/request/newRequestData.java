package org.ioad.spring.request.payload.request;

import jakarta.validation.constraints.NotBlank;
import org.ioad.spring.resource.models.ResourceType;

public class newRequestData {
    @NotBlank
    private String description;

    @NotBlank
    private Double latitude;
    @NotBlank
    private Double longitude;

    @NotBlank
    private ResourceType resourceType;

    @NotBlank
    private Integer amount;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
