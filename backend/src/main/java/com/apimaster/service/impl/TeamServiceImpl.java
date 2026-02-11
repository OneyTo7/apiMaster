package com.apimaster.service.impl;

import com.apimaster.entity.Team;
import com.apimaster.entity.TeamMember;
import com.apimaster.repository.TeamMemberRepository;
import com.apimaster.repository.TeamRepository;
import com.apimaster.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamServiceImpl(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public Team createTeam(Team team) {
        Team savedTeam = teamRepository.save(team);
        
        // 创建者自动成为团队成员，角色为所有者
        TeamMember member = new TeamMember();
        member.setTeamId(savedTeam.getId());
        member.setUserId(savedTeam.getCreatorId());
        member.setRole(1); // 1: 所有者
        teamMemberRepository.save(member);
        
        return savedTeam;
    }

    @Override
    public List<Team> getTeams() {
        return teamRepository.findAll();
    }

    @Override
    public List<Team> getTeamsByUserId(Long userId) {
        logger.info("开始获取用户团队列表，用户ID: {}", userId);
        try {
            // 获取用户所在的所有团队
            List<TeamMember> teamMembers = teamMemberRepository.findByUserId(userId);
            logger.info("获取到用户团队成员数量: {}", teamMembers.size());
            
            List<Team> teams = new ArrayList<>();
            for (TeamMember member : teamMembers) {
                logger.info("处理团队成员，团队ID: {}", member.getTeamId());
                Team team = teamRepository.findById(member.getTeamId()).orElse(null);
                if (team != null) {
                    teams.add(team);
                }
            }
            
            logger.info("获取用户团队列表完成，团队数量: {}", teams.size());
            return teams;
        } catch (Exception e) {
            logger.error("获取用户团队列表失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Team getTeamById(Long id) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        return optionalTeam.orElse(null);
    }

    @Override
    public Team updateTeam(Long id, Team team) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            Team existingTeam = optionalTeam.get();
            existingTeam.setName(team.getName());
            existingTeam.setDescription(team.getDescription());
            existingTeam.setStatus(team.getStatus());
            return teamRepository.save(existingTeam);
        }
        return null;
    }

    @Override
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    @Override
    public TeamMember addTeamMember(Long teamId, TeamMember member) {
        member.setTeamId(teamId);
        return teamMemberRepository.save(member);
    }

    @Override
    public void removeTeamMember(Long teamId, Long userId) {
        teamMemberRepository.deleteByTeamIdAndUserId(teamId, userId);
    }

    @Override
    public boolean isTeamMember(Long teamId, Long userId) {
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
        return member != null;
    }

    @Override
    public boolean isTeamOwnerOrAdmin(Long teamId, Long userId) {
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
        return member != null && (member.getRole() == 1 || member.getRole() == 2);
    }
}
