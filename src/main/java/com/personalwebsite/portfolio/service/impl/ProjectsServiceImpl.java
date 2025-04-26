package com.personalwebsite.portfolio.service.impl;

import com.personalwebsite.portfolio.dto.request.ProjectRequest;
import com.personalwebsite.portfolio.entity.ProjectImage;
import com.personalwebsite.portfolio.entity.Projects;
import com.personalwebsite.portfolio.entity.User;
import com.personalwebsite.portfolio.exception.ApiErrorException;
import com.personalwebsite.portfolio.repository.ProjectImageRepository;
import com.personalwebsite.portfolio.repository.ProjectsRepository;
import com.personalwebsite.portfolio.repository.UserRepository;
import com.personalwebsite.portfolio.service.FileStorageService;
import com.personalwebsite.portfolio.service.ProjectsService;
import com.personalwebsite.portfolio.service.S3Service;
import com.personalwebsite.portfolio.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectsServiceImpl implements ProjectsService {
    private final ProjectsRepository projectsRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final FileStorageService fileStorageService;
    private final ProjectImageRepository projectImageRepository;
    private final S3Service s3Service;

    @Value("${aws.cloudfront.domain}")
    private String cloudfrontDomain;

    @Override
    public List<Projects> getAllProject() {
        List<Projects> projects=projectsRepository.findAll();

        for (Projects project : projects) {
            if (project.getProjectImages() != null) {
                for (ProjectImage image : project.getProjectImages()) {
                    if (image.getImageUrl() != null && image.getImageUrl().contains("amazonaws.com")) {
                        // S3 URL'sinden dosya adını çıkar
                        String objectKey = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
                        // CloudFront URL'si oluştur
                        String cloudfrontUrl = "https://" + cloudfrontDomain + "/" + objectKey;
                        // CloudFront URL'sini set et
                        image.setImageUrl(cloudfrontUrl);
                    }
                }
            }
        }
        return  projects;
    }

    @Override
    public Projects getProjectWithById(Long id) {
        Projects projects=projectsRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Project not found!"));
        return projects;
    }

    @Override
    @Transactional
    public Projects createProjects(ProjectRequest projectRequest, MultipartFile media) {
        User currentUser=securityUtils.getCurrentUser();

        Projects newProject=new Projects();
        newProject.setProjectName(projectRequest.getProjectName());
        newProject.setProjectDescription(projectRequest.getProjectDescription());
        newProject.setProjectGithubUrl(projectRequest.getProjectGithubUrl());
        newProject.setProjectLiveUrl(projectRequest.getProjectLiveUrl());

        Projects savedProjects=projectsRepository.save(newProject);

       if(media!=null&&!media.isEmpty()){
           String s3Url=s3Service.uploadFile(media);


           if(s3Url!=null){

               String objectKey = s3Url.substring(s3Url.lastIndexOf("/") + 1);
               String cloudfrontUrl = "https://" + cloudfrontDomain + "/" + objectKey;
               ProjectImage projectImage=new ProjectImage();
               projectImage.setImageUrl(cloudfrontUrl);
               projectImage.setOrder(1);
               projectImage.setProject(savedProjects);

               projectImageRepository.save(projectImage);
           }
       }

        return savedProjects;
    }

    @Override
    @Transactional
    public Projects addProjectImages(Long projectId, List<MultipartFile> images) {
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        int order = project.getProjectImages().size() + 1;

        for (MultipartFile image : images) {
            String s3Url = s3Service.uploadFile(image);

            if (s3Url != null) {
                // S3 URL'sinden dosya adını çıkar
                String objectKey = s3Url.substring(s3Url.lastIndexOf("/") + 1);
                // CloudFront URL'si oluştur
                String cloudfrontUrl = "https://" + cloudfrontDomain + "/" + objectKey;

                ProjectImage projectImage = new ProjectImage();
                projectImage.setImageUrl(cloudfrontUrl);
                projectImage.setOrder(order++);
                projectImage.setProject(project);

                projectImageRepository.save(projectImage);
            }
        }

        return projectsRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
}


