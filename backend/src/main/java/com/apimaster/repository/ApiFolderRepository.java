package com.apimaster.repository;

import com.apimaster.entity.ApiFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiFolderRepository extends JpaRepository<ApiFolder, Long> {

    List<ApiFolder> findByProjectIdAndParentIdIsNullOrderBySortAsc(Long projectId);

    List<ApiFolder> findByProjectIdAndParentIdOrderBySortAsc(Long projectId, Long parentId);

    List<ApiFolder> findByProjectIdOrderBySortAsc(Long projectId);

    void deleteByProjectId(Long projectId);
}
