package com.personalwebsite.portfolio.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file, String directory);
    boolean deleteFile(String fileUrl);
}
