package com.dohui.user_service.domain.post.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Profile("!test")
@RequestMapping("/api/files")
public class FileController {
    private final String uploadDir = "C:/upload/";

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String filename) throws Exception {

        Path path = Paths.get(uploadDir).resolve(filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("파일 없음");
        }

        String encodedName = URLEncoder.encode(resource.getFilename(), "UTF-8")
                .replace("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedName + "\"")
                .body(resource);
    }
}
