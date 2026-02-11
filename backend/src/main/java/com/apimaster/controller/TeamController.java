package com.apimaster.controller;

import com.apimaster.entity.Team;
import com.apimaster.entity.TeamMember;
import com.apimaster.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * 创建团队
     */
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        Team createdTeam = teamService.createTeam(team);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    /**
     * 获取团队列表
     */
    @GetMapping
    public ResponseEntity<List<Team>> getTeams() {
        List<Team> teams = teamService.getTeams();
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    /**
     * 获取用户所在的团队列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Team>> getTeamsByUserId(@PathVariable Long userId) {
        List<Team> teams = teamService.getTeamsByUserId(userId);
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    /**
     * 获取团队详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamById(id);
        if (team != null) {
            return new ResponseEntity<>(team, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 更新团队
     */
    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id, @RequestBody Team team) {
        Team updatedTeam = teamService.updateTeam(id, team);
        if (updatedTeam != null) {
            return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 删除团队
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 获取团队成员
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<TeamMember>> getTeamMembers(@PathVariable Long id) {
        List<TeamMember> members = teamService.getTeamMembers(id);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    /**
     * 添加团队成员
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<TeamMember> addTeamMember(@PathVariable Long id, @RequestBody TeamMember member) {
        TeamMember addedMember = teamService.addTeamMember(id, member);
        return new ResponseEntity<>(addedMember, HttpStatus.CREATED);
    }

    /**
     * 移除团队成员
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeTeamMember(@PathVariable Long id, @PathVariable Long userId) {
        teamService.removeTeamMember(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
