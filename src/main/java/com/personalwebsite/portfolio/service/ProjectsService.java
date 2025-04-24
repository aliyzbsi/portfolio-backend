package com.personalwebsite.portfolio.service;


import com.personalwebsite.portfolio.dto.request.ProjectRequest;
import com.personalwebsite.portfolio.entity.Projects;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectsService {

    List<Projects> getAllProject();

    Projects getProjectWithById(Long id);
    Projects createProjects(ProjectRequest projectRequest, MultipartFile media);

    Projects addProjectImages(Long projectId, List<MultipartFile> images);
}
