package com.dohui.user_service.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequest {
    @NotBlank(message = "댓글을 입력해주세요")
    private String content;
    private Long parentId;
}
