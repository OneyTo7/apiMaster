package com.apimaster.repository;

import com.apimaster.entity.ApiVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiVersionRepository extends JpaRepository<ApiVersion, Long> {

    List<ApiVersion> findByApiId(Long apiId);

    ApiVersion findByApiIdAndVersion(Long apiId, String version);
}
