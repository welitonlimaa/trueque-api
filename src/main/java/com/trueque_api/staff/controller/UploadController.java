package com.trueque_api.staff.controller;

import com.trueque_api.staff.service.CloudinaryService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(
        value = "/images",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<List<String>> uploadImages(
        @RequestPart("files") List<MultipartFile> files
    ) {
        List<String> urls = files.stream()
            .map(cloudinaryService::upload)
            .toList();

        return ResponseEntity.ok(urls);
    }
}