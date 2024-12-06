package org.ioad.resource.models;

import jakarta.persistence.*;

import java.time.LocalDate;

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
}
