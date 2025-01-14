package org.ioad.spring.resource.models;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@DiscriminatorValue("DONATION")
public class Donation extends Resource {
    @Column(updatable=false)
    private Long donorId;

    public Donation(Long id, String name, String description, Location location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType type, Long donorId) {
        super(id, description, name, location, expDate, quantity, unit, organisationId, type);
        this.donorId = donorId;
    }

    public Donation() {
    }

    public Donation(String name, String description, Location location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType type, Long donorId) {
        super(name, description, location, expDate, quantity, unit, organisationId, type);
        this.donorId = donorId;
    }
}
