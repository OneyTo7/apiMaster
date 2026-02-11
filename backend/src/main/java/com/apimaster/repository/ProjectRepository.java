package com.apimaster.repository;

import com.apimaster.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByCreatorId(Long creatorId);

    List<Project> findByStatus(Integer status);

    List<Project> findByCreatorIdAndStatus(Long creatorId, Integer status);
}
