package org.ioad.spring.request.models;

import jakarta.persistence.*;
import org.ioad.spring.resource.models.ResourceType;
import org.ioad.spring.user.models.UserInfo;

@Entity
@Table( name = "requests")

public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    private String description;
    private Double latitude;
    private Double longitude;
    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)

    private UserInfo reporter;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ResourceType resourceType;
    private Integer amount;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus status;

    private String resourceName;

    public Request(String description, double latitude, double longitude, UserInfo reporter, ResourceType resourceType, int amount, String resourceName) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reporter = reporter;
        this.resourceType = resourceType;
        this.amount = amount;
        this.status = EStatus.REGISTERED;
        this.resourceName = resourceName;
    }

    public Request() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public String getDescription() {
        return description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public UserInfo getReporter() {
        return reporter;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Integer getAmount() {
        return amount;
    }

    public EStatus getStatus() {
        return status;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

}
