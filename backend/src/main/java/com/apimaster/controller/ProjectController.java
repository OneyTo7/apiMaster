package com.apimaster.controller;

import com.apimaster.entity.Project;
import com.apimaster.entity.ProjectMember;
import com.apimaster.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 创建项目
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    /**
     * 获取项目列表
     */
    @GetMapping
    public ResponseEntity<List<Project>> getProjects() {
        List<Project> projects = projectService.getProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        if (project != null) {
            return new ResponseEntity<>(project, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 更新项目
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        Project updatedProject = projectService.updateProject(id, project);
        if (updatedProject != null) {
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 获取项目成员
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMember>> getProjectMembers(@PathVariable Long id) {
        List<ProjectMember> members = projectService.getProjectMembers(id);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    /**
     * 添加项目成员
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<ProjectMember> addProjectMember(@PathVariable Long id, @RequestBody ProjectMember member) {
        ProjectMember addedMember = projectService.addProjectMember(id, member);
        return new ResponseEntity<>(addedMember, HttpStatus.CREATED);
    }

    /**
     * 移除项目成员
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeProjectMember(@PathVariable Long id, @PathVariable Long userId) {
        projectService.removeProjectMember(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
