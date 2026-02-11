package com.apimaster.repository;

import com.apimaster.entity.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiRepository extends JpaRepository<Api, Long> {

    List<Api> findByProjectId(Long projectId);

    List<Api> findByProjectIdAndStatus(Long projectId, Integer status);

    List<Api> findByMethod(String method);

    List<Api> findByPathContaining(String path);

    List<Api> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Api> findByFolderIdOrderByCreatedAtDesc(Long folderId);

    List<Api> findByProjectIdAndNameContainingOrPathContainingOrderByCreatedAtDesc(
            Long projectId, String name, String path);
}
