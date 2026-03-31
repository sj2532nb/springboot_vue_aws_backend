package com.dohui.user_service.domain.post.dto.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {
    @Id
    @GeneratedValue
    private Long id;

    private String originalName;
    private String fileName;
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public Attachment(String originalName, String fileName, String url, Post post){
        this.originalName = originalName;
        this.fileName = fileName;
        this.url = url;
        this.post = post;
    }
}
