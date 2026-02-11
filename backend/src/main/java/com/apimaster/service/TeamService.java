package com.apimaster.service;

import com.apimaster.entity.Team;
import com.apimaster.entity.TeamMember;

import java.util.List;

public interface TeamService {

    Team createTeam(Team team);

    List<Team> getTeams();

    List<Team> getTeamsByUserId(Long userId);

    Team getTeamById(Long id);

    Team updateTeam(Long id, Team team);

    void deleteTeam(Long id);

    List<TeamMember> getTeamMembers(Long teamId);

    TeamMember addTeamMember(Long teamId, TeamMember member);

    void removeTeamMember(Long teamId, Long userId);

    boolean isTeamMember(Long teamId, Long userId);

    boolean isTeamOwnerOrAdmin(Long teamId, Long userId);
}
