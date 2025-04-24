package com.personalwebsite.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "Proje adı boş olamaz")
    private String projectName;

    @NotBlank(message = "Proje açıklaması boş olamaz")
    private String projectDescription;

    private String projectGithubUrl;

    private String projectLiveUrl;
}
