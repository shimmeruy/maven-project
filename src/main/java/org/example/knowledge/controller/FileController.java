package org.example.knowledge.controller;

import org.example.knowledge.dto.ApiResponse;
import org.example.knowledge.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/knowledge/data")
public class FileController {

    @Autowired
    private MinioService minioService;

    @PostMapping("/upload-image")
    public ApiResponse<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/svg+xml"};
        String contentType = file.getContentType();
        boolean valid = false;
        for (String t : allowedTypes) {
            if (t.equals(contentType)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            return ApiResponse.error(400, "Invalid file type. Allowed: jpg, png, gif, svg");
        }
        String path = minioService.upload(file);
        String url = minioService.getAccessUrl(path);
        Map<String, String> result = new HashMap<>();
        result.put("path", path);
        result.put("url", url);
        return ApiResponse.success(result);
    }
}
