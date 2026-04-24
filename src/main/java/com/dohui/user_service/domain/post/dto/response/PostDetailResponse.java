package com.dohui.user_service.domain.post.dto.response;

import com.dohui.user_service.domain.post.dto.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private boolean isAuthor;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private int commentCount;
    private int likeCount;
    private boolean liked;
    private int viewCount;

    private List<AttachmentResponse> attachments;

    @JsonProperty("isPrivate")
    private boolean isPrivate;

    public static PostDetailResponse from(
            Post post,
            int likeCount,
            boolean liked,
            boolean isAuthor
    ){
        List<AttachmentResponse> attachments = post.getAttachments().stream()
                .map(AttachmentResponse::from)
                .toList();

        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getUsername())
                .isAuthor(isAuthor)
                .createdAt(post.getCreatedAt())
                .commentCount(post.getCommentCount())
                .likeCount(likeCount)
                .liked(liked)
                .viewCount(post.getViewCount())
                .attachments(attachments)
                .isPrivate(post.isPrivate())
                .build();
    }
}
