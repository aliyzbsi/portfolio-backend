package com.personalwebsite.portfolio.repository;

import com.personalwebsite.portfolio.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectImageRepository extends JpaRepository<ProjectImage,Long> {

    List<ProjectImage> findByProjectIdOrderByOrderAsc(Long projectId);
}
