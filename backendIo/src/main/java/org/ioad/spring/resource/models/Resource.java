package org.ioad.spring.resource.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "resources")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("RESOURCE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Resource {
    @Getter
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
    @Getter
    @Column(nullable = false, updatable = false)
    private String name;
    @Getter
    @Setter
    private String description;
    @Setter
    @Getter
    @Column(nullable = false)
    @Embedded
    private Location location;
    @Getter
    @Column(updatable = false)
    private LocalDate expDate;
    @Setter
    @Getter
    @Column(nullable = false)
    private Double quantity;
    @Getter
    @Column(nullable = false, updatable = false)
    private String unit;
    @Getter
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate addedDate;
    @Getter
    @Column(nullable = false)
    private Long organisationId;
    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status = ResourceStatus.AVAILABLE;
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ResourceType resourceType;

    public Resource(Long id, String name, String description, Location location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType resourceType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.expDate = expDate;
        this.quantity = quantity;
        this.unit = unit;
        this.organisationId = organisationId;
        this.resourceType = resourceType;
    }

    public Resource() {
    }

    public Resource(String name, String description, Location location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType resourceType) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.expDate = expDate;
        this.quantity = quantity;
        this.unit = unit;
        this.organisationId = organisationId;
        this.resourceType = resourceType;
    }

    public void makeAvailable() {
        this.setStatus(ResourceStatus.AVAILABLE);
    }

    public void fullyAssigned() {
        this.setStatus(ResourceStatus.FULLY_ASSIGNED);
    }

    public boolean isExpired() {
        return this.status == ResourceStatus.EXPIRED;
    }

    public boolean isDamaged() {
        return this.status == ResourceStatus.DAMAGED;
    }
}
