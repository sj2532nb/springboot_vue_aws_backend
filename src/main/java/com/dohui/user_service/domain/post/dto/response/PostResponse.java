package com.dohui.user_service.domain.post.dto.response;

import com.dohui.user_service.domain.post.dto.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String authorNickname;
    private LocalDateTime createdAt;
    private int commentCount;
    private int likeCount;
    private int viewCount;
    private boolean author;

    @JsonProperty("isPrivate")
    private boolean isPrivate;

    public static PostResponse from(Post post, Long uid){
        boolean author = uid != null && post.getUser().getId().equals(uid);

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUsername(),
                post.getCreatedAt(),
                post.getCommentCount(),
                post.getLikeCount(),
                post.getViewCount(),
                author,
                post.isPrivate()
        );
    }
}