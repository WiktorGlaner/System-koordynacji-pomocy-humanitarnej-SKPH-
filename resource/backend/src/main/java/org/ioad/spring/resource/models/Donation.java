package org.ioad.spring.resource.models;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("DONATION")
public class Donation extends Resource {
    @Column(updatable=false)
    private Long donorId;

    public Donation(Long id, String name, String location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType type, Long donorId) {
        super(id, name, location, expDate, quantity, unit, organisationId, type);
        this.donorId = donorId;
    }

    public Donation() {
    }

    public Donation(String name, String location, LocalDate expDate, Double quantity, String unit, Long organisationId, ResourceType type, Long donorId) {
        super(name, location, expDate, quantity, unit, organisationId, type);
        this.donorId = donorId;
    }

    public Long getDonorId() {
        return donorId;
    }


}
