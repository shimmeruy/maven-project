package org.example.knowledge.service;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private String minioBucketName;

    public String upload(MultipartFile file) {
        try {
            String objectName = UUID.randomUUID().toString().replace("-", "")
                    + "_" + file.getOriginalFilename();
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioBucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucketName).build());
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return minioBucketName + "/" + objectName;
        } catch (Exception e) {
            throw new RuntimeException("MinIO upload failed: " + e.getMessage(), e);
        }
    }

    public String getAccessUrl(String objectPath) {
        if (objectPath == null || !objectPath.contains("/")) {
            return objectPath;
        }
        try {
            String[] parts = objectPath.split("/", 2);
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(parts[0])
                            .object(parts[1])
                            .method(Method.GET)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("MinIO get URL failed: " + e.getMessage(), e);
        }
    }

    public void delete(String objectPath) {
        if (objectPath == null || !objectPath.contains("/")) {
            return;
        }
        try {
            String[] parts = objectPath.split("/", 2);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(parts[0])
                            .object(parts[1])
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("MinIO delete failed: " + e.getMessage(), e);
        }
    }
}
