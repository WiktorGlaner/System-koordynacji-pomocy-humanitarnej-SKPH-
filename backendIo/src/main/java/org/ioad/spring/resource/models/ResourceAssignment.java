package org.ioad.spring.resource.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table
public class ResourceAssignment {
    @Id
    @SequenceGenerator(
            name = "resourceassignment_sequence",
            sequenceName = "resourceassignment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "resourceassignment_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
    private Resource resource;

    @Column(nullable = false)
    private Long requestId;

    @Column(nullable = false, updatable = false)
    private double assignedQuantity;

    @Column(nullable = false, updatable = false)
    private LocalDate assignmentDate;

    public ResourceAssignment(Long id, Resource resource, Long requestId, double assignedQuantity) {
        this.id = id;
        this.resource = resource;
        this.requestId = requestId;
        this.assignedQuantity = assignedQuantity;
        this.assignmentDate = LocalDate.now();
    }

    public ResourceAssignment() {
    }

    public ResourceAssignment(Resource resource, Long requestId, double assignedQuantity) {
        this.resource = resource;
        this.requestId = requestId;
        this.assignedQuantity = assignedQuantity;
        this.assignmentDate = LocalDate.now();
    }
}
