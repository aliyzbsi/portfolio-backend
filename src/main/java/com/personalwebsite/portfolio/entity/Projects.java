package com.personalwebsite.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "projects",schema = "portfolio")
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_desc", columnDefinition = "TEXT")
    private String projectDescription;

    @JsonManagedReference
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ProjectImage> projectImages=new ArrayList<>();

    @Column(name = "project_github_url")
    private String projectGithubUrl;

    @Column(name = "project_live_url")
    private String projectLiveUrl;

    public void addImage(ProjectImage image){
        projectImages.add(image);
        image.setProject(this);
    }

    public void removeImage(ProjectImage image){
        projectImages.remove(image);
        image.setProject(null);
    }

}
