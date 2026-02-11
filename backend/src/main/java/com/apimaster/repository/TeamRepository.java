package com.apimaster.repository;

import com.apimaster.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByCreatorId(Long creatorId);

    List<Team> findByStatus(Integer status);

    List<Team> findByCreatorIdAndStatus(Long creatorId, Integer status);
}
