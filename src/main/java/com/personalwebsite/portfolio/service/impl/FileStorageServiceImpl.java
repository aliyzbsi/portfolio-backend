package com.personalwebsite.portfolio.service.impl;

import com.personalwebsite.portfolio.service.FileStorageService;
import com.personalwebsite.portfolio.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final S3Service s3Service;

    @Override
    public String storeFile(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()){
            return null;
        }

        try {
            String fileUrl=s3Service.uploadFile(file);
            return fileUrl;
        } catch (Exception e) {
            throw new RuntimeException("Could not store file", e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        try {
            String fileName=fileUrl.substring(fileUrl.lastIndexOf("/")+1);
            s3Service.deleteFile(fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
