package com.dohui.user_service.domain.post.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final String uploadDir = "C:/upload/";

    @PostMapping("/image")
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException{
        // 파일 없으면 예외
        if(file.isEmpty()){
            throw new RuntimeException("파일이 없습니다");
        }

        // 파일명 중복 방지
        String originalName = file.getOriginalFilename();

        if (!originalName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
            throw new RuntimeException("이미지 파일만 업로드 해주세요");
        }

        if(originalName == null){
            originalName = "image.png";
        }

        String fileName = UUID.randomUUID() + "_" + originalName;

        // 폴더 없으면 생성
        File dir = new File(uploadDir);
        if(!dir.exists()){
            dir.mkdirs();
        }

        // 파일 저장
        File dest = new File(uploadDir + fileName);
        file.transferTo(dest);

        // URL 반환
        String url = "http://localhost:8080/images/" + fileName;

        return Map.of("url", url);
    }

    @PostMapping("/file")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("파일이 없습니다");
        }

        String originalName = file.getOriginalFilename();

        if (!originalName.matches(".*\\.(pdf|zip|txt|docx)$")) {
            throw new RuntimeException("허용되지 않은 파일입니다");
        }

        if (originalName == null) {
            originalName = "file";
        }

        String fileName = UUID.randomUUID() + "_" + originalName;

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(uploadDir + fileName);
        file.transferTo(dest);

        String url = "http://localhost:8080/files/" + fileName;

        return Map.of(
                "url", url,
                "name", originalName
        );
    }
}
