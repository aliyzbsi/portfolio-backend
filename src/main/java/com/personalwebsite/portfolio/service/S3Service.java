package com.personalwebsite.portfolio.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    private final AmazonS3 s3Client;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif","image/webp","image,jpg"
    );
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

    public S3Service(AmazonS3 s3Client){
        this.s3Client=s3Client;
    }

    public String uploadFile(MultipartFile file){
        File fileObject=null;
        try {
            fileObject=convertMultiPartFile(file);

            String fileName=generateUniqueFileName(file);

            ObjectMetadata metadata=new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest=new PutObjectRequest(bucketName,fileName,fileObject)
                    .withMetadata(metadata);

            s3Client.putObject(putObjectRequest);

            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, fileName);

            return fileUrl;
        } catch (Exception e) {
            throw new RuntimeException("Dosya yüklenemedi : "+e.getMessage());
        }finally {
            if (fileObject!=null && fileObject.exists()){
                fileObject.delete();
            }
        }
    }

    private String generateUniqueFileName(MultipartFile file) {

        String originalFileName=file.getOriginalFilename();
        String extension=originalFileName!=null?
                originalFileName.substring(originalFileName.lastIndexOf(".")):"";
        return UUID.randomUUID().toString()+extension;

    }

    private File convertMultiPartFile(MultipartFile file) {

        String originalFilename=file.getOriginalFilename();
        if(originalFilename==null){
            throw new RuntimeException("Original file name is null");
        }
        File convertedFile=new File(originalFilename);
        try (FileOutputStream fos=new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
            return convertedFile;
        }catch (IOException e){
            throw new RuntimeException("Failed to convert file: "+e.getMessage());
        }

    }
    public void deleteFile(String fileName) {

        try {
            if (!s3Client.doesObjectExist(bucketName, fileName)) {

                return;
            }

            s3Client.deleteObject(bucketName, fileName);

        } catch (AmazonServiceException e) {

            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }
}
