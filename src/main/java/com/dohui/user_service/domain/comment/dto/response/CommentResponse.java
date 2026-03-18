package com.dohui.user_service.domain.comment.dto.response;

import com.dohui.user_service.domain.comment.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class CommentResponse {
    private final Long id;
    private final String content;
    private final Long userId;
    private final String username;  // 댓글 작성자 닉네임
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private final Long parentId;
    private final boolean deleted;

    private List<CommentResponse> replies;

    public static CommentResponse from(Comment comment){
        String content = comment.isDeleted() ? "삭제된 댓글입니다" : comment.getContent();

        return CommentResponse.builder()
                .id(comment.getId())
                .content(content)
                .userId(comment.getUser().getId())
                .username(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .deleted(comment.isDeleted())
                .replies(new ArrayList<>())
                .build();
    }
}
