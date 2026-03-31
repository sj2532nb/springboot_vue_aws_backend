package com.dohui.user_service.domain.comment.dto.response;

import com.dohui.user_service.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyCommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long postId;

    public static MyCommentResponse from(Comment comment){
        return new MyCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getPost().getId()
        );
    }
}
