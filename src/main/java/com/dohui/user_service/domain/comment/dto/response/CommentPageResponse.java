package com.dohui.user_service.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommentPageResponse {
    private List<CommentResponse> content;
    private int totalPages;
    private long totalElements;
}
