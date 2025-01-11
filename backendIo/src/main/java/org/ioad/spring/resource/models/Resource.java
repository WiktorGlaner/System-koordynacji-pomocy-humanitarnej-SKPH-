package org.ioad.spring.resource.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "resources")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("RESOURCE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Resource {
    @Id
    @SequenceGenerator(
            name = "resource_sequence",
            sequenceName = "resource_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "resource_sequence"
    )
    @Column(name = "resource_id")
    private Long id;
    @Column(nullable = false, updatable = false)
    private String name;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false, updatable = false)
    private LocalDate expDate;
    @Column(nullable = false, updatable = false)
    private Double quantity;
    @Column(nullable = false, updatable = false)
    private String unit;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate addedDate;
    private Long organisationId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status = ResourceStatus.AVAILABLE;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ResourceType resourceType;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getExpDate() {
        return expDate;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Resource(Long id, String name, String location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType resourceType) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.expDate = expDate;
        this.quantity = quantity;
        this.unit = unit;
        this.organisationId = organisationId;
        this.resourceType = resourceType;
    }

    public Resource() {
    }

    public Resource(String name, String location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType resourceType) {
        this.name = name;
        this.location = location;
        this.expDate = expDate;
        this.quantity = quantity;
        this.unit = unit;
        this.organisationId = organisationId;
        this.resourceType = resourceType;
    }

    public void restoreResource() {
        this.setStatus(ResourceStatus.AVAILABLE);
    }

    public void outOfComission() {
        this.setStatus(ResourceStatus.UNAVAILABLE);
    }

    public boolean isExpired() {
        boolean isExpired = this.getExpDate().isBefore(LocalDate.now());
        if (isExpired) {
            this.setStatus(ResourceStatus.EXPIRED);
        }
        return isExpired;
    }
}
