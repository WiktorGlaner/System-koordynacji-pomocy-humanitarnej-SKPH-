package org.ioad.spring.task.service;

import org.ioad.spring.task.model.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecificationService {

    // Filtruje po location
    public static Specification<Task> hasLocation(String location) {
        return (root, query, criteriaBuilder) ->
                location == null ? null : criteriaBuilder.equal(root.get("location"), location);
    }

    // Filtruje po grade (może być null, bo w zadaniu może brakować oceny)
    public static Specification<Task> hasGrade(Integer grade) {
        return (root, query, criteriaBuilder) ->
                grade == null ? null : criteriaBuilder.equal(root.get("grade"), grade);
    }

    // Filtruje po organization
    public static Specification<Task> hasOrganization(String organization) {
        return (root, query, criteriaBuilder) ->
                organization == null ? null : criteriaBuilder.equal(root.get("organization"), organization);
    }

    // Filtruje po priority
    public static Specification<Task> hasPriority(Integer priority) {
        return (root, query, criteriaBuilder) ->
                priority == null ? null : criteriaBuilder.equal(root.get("priority"), priority);
    }

    // Filtruje po statusie
    public static Specification<Task> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }
}