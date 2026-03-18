package com.dohui.user_service.domain.comment;

import com.dohui.user_service.domain.comment.dto.CommentRequest;
import com.dohui.user_service.domain.comment.dto.response.CommentPageResponse;
import com.dohui.user_service.domain.comment.dto.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}")
    public Long create(
            @PathVariable
            Long postId,
            @RequestBody CommentRequest request,
            Authentication authentication
    ){
        Long userId = Long.valueOf(authentication.getName());
        return commentService.create(userId, postId, request.getContent(), request.getParentId());
    }

    // 댓글 목록
    @GetMapping("/post/{postId}")
    public CommentPageResponse getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return commentService.findByPost(postId, page, size);
    }


    // 댓글 수정
    @PutMapping("/{commentId}")
    public Long update(
            @PathVariable
            Long commentId,
            @RequestBody
            CommentRequest request,
            Authentication authentication
    ){
        Long userId = Long.valueOf(authentication.getName());
        return commentService.update(userId, commentId, request.getContent());
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public Long delete(
            @PathVariable
            Long commentId,
            Authentication authentication
    ){
        Long userId = Long.valueOf(authentication.getName());
        return commentService.delete(userId, commentId);
    }
}
