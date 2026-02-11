package com.apimaster.service.impl;

import com.apimaster.entity.Project;
import com.apimaster.entity.ProjectMember;
import com.apimaster.repository.ProjectMemberRepository;
import com.apimaster.repository.ProjectRepository;
import com.apimaster.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    public Project createProject(Project project) {
        Project savedProject = projectRepository.save(project);
        
        // 创建者自动成为项目成员，角色为所有者
        ProjectMember member = new ProjectMember();
        member.setProjectId(savedProject.getId());
        member.setUserId(savedProject.getCreatorId());
        member.setRole(1); // 1: 所有者
        projectMemberRepository.save(member);
        
        return savedProject;
    }

    @Override
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        return optionalProject.orElse(null);
    }

    @Override
    public Project updateProject(Long id, Project project) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()) {
            Project existingProject = optionalProject.get();
            existingProject.setName(project.getName());
            existingProject.setDescription(project.getDescription());
            existingProject.setStatus(project.getStatus());
            return projectRepository.save(existingProject);
        }
        return null;
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public List<ProjectMember> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }

    @Override
    public ProjectMember addProjectMember(Long projectId, ProjectMember member) {
        member.setProjectId(projectId);
        return projectMemberRepository.save(member);
    }

    @Override
    public void removeProjectMember(Long projectId, Long userId) {
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public boolean isProjectMember(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return member != null;
    }

    @Override
    public boolean isProjectOwnerOrAdmin(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        return member != null && (member.getRole() == 1 || member.getRole() == 2);
    }
}
