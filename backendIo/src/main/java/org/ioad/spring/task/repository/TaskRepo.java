package org.ioad.spring.task.repository;


import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.task.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TaskRepo")
public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findAll(Specification<Task> spec);

    @Query("SELECT t FROM Task t WHERE t.id = :id")
    Resource findByTaskId(@Param("id") Long id);
}
