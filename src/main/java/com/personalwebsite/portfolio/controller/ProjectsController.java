package com.personalwebsite.portfolio.controller;

import com.personalwebsite.portfolio.dto.request.ProjectRequest;
import com.personalwebsite.portfolio.dto.response.ApiResponse;
import com.personalwebsite.portfolio.entity.Projects;
import com.personalwebsite.portfolio.service.ProjectsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectsController {

    private final ProjectsService projectsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Projects>>> getAllProjects(){
        List<Projects> projects=projectsService.getAllProject();

        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Projects>> getProjectWithById(
            @PathVariable("id") Long id
    ){

        return ResponseEntity.ok(ApiResponse.success(projectsService.getProjectWithById(id)));

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Projects>> createProjects(
            @RequestParam("projectName") String projectName,
            @RequestParam("projectDescription") String projectDescription,
            @RequestParam(value = "projectGithubUrl", required = false) String projectGithubUrl,
            @RequestParam(value = "projectLiveUrl", required = false) String projectLiveUrl,
            @RequestPart(value = "media", required = false) MultipartFile media
    ){


        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectName(projectName);
        projectRequest.setProjectDescription(projectDescription);
        projectRequest.setProjectGithubUrl(projectGithubUrl);
        projectRequest.setProjectLiveUrl(projectLiveUrl);

        Projects createdProjects=projectsService.createProjects(projectRequest, media);

        return ResponseEntity.ok(ApiResponse.success("Proje başarıyla oluşturuldu", createdProjects));
    }
    @PostMapping(value = "/{projectId}/images",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Projects>> addProjectImage(
            @PathVariable("projectId") Long projectId,
            @RequestParam("images") List<MultipartFile> images
    ){
        Projects updatedProject=projectsService.addProjectImages(projectId,images);
        return ResponseEntity.ok(ApiResponse.success("Resimler Başarıyla eklendi",updatedProject));
    }

}
