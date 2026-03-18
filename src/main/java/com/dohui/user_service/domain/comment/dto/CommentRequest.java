package com.dohui.user_service.domain.comment.dto;

import lombok.Getter;

@Getter
public class CommentRequest {
    private String content;
    private Long parentId;
}
