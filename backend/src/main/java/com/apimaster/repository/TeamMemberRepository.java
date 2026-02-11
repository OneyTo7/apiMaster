package com.apimaster.repository;

import com.apimaster.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamId(Long teamId);

    List<TeamMember> findByUserId(Long userId);

    TeamMember findByTeamIdAndUserId(Long teamId, Long userId);

    void deleteByTeamIdAndUserId(Long teamId, Long userId);
}
