package com.personalwebsite.portfolio.repository;

import com.personalwebsite.portfolio.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectsRepository extends JpaRepository<Projects,Long> {
}
