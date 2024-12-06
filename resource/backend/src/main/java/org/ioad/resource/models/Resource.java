package org.ioad.resource.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
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
    private LocalDate addedDate;
    private Long organisationId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ResourceType type;

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

    public ResourceType getType() {
        return type;
    }

    @PrePersist
    protected void onCreate() {
        addedDate = LocalDate.now();
        status = ResourceStatus.AVAILABLE;
    }

    public Resource(Long id, String name, String location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType type) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.expDate = expDate;
        this.quantity = quantity;
        this.unit = unit;
        this.organisationId = organisationId;
        this.type = type;
    }

    public Resource() {
    }

    public Resource(String name, String location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType type) {
        this.name = name;
        this.location = location;
        this.expDate = expDate;
        this.quantity = quantity;
        this.unit = unit;
        this.organisationId = organisationId;
        this.type = type;
    }

    public void restoreResource() {
        this.setStatus(ResourceStatus.AVAILABLE);
    }

    public void outOfComission() {
        this.setStatus(ResourceStatus.EXPIRED);
    }

    public boolean isExpired() {
        return this.getStatus() == ResourceStatus.EXPIRED;
    }
}
