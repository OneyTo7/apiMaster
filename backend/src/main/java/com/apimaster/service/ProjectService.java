package com.apimaster.service;

import com.apimaster.entity.Project;
import com.apimaster.entity.ProjectMember;

import java.util.List;

public interface ProjectService {

    /**
     * 创建项目
     * @param project 项目信息
     * @return 创建的项目
     */
    Project createProject(Project project);

    /**
     * 获取项目列表
     * @return 项目列表
     */
    List<Project> getProjects();

    /**
     * 获取项目详情
     * @param id 项目ID
     * @return 项目详情
     */
    Project getProjectById(Long id);

    /**
     * 更新项目
     * @param id 项目ID
     * @param project 项目信息
     * @return 更新后的项目
     */
    Project updateProject(Long id, Project project);

    /**
     * 删除项目
     * @param id 项目ID
     */
    void deleteProject(Long id);

    /**
     * 获取项目成员
     * @param projectId 项目ID
     * @return 项目成员列表
     */
    List<ProjectMember> getProjectMembers(Long projectId);

    /**
     * 添加项目成员
     * @param projectId 项目ID
     * @param member 项目成员信息
     * @return 添加的项目成员
     */
    ProjectMember addProjectMember(Long projectId, ProjectMember member);

    /**
     * 移除项目成员
     * @param projectId 项目ID
     * @param userId 用户ID
     */
    void removeProjectMember(Long projectId, Long userId);

    /**
     * 检查用户是否是项目成员
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return 是否是项目成员
     */
    boolean isProjectMember(Long projectId, Long userId);

    /**
     * 检查用户是否是项目所有者或管理员
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return 是否是项目所有者或管理员
     */
    boolean isProjectOwnerOrAdmin(Long projectId, Long userId);
}
