package com.dohui.user_service.domain.post;

import com.dohui.user_service.domain.post.dto.request.PostRequest;
import com.dohui.user_service.domain.post.dto.response.PostDetailResponse;
import com.dohui.user_service.domain.post.dto.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    // 글 작성
    @PostMapping
    public Long create(
            @RequestBody
            PostRequest request,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return postService.create(userId, request.getTitle(), request.getContent());
    }

    // 글 목록
    @GetMapping
    public Page<PostResponse> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "all") String type,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication
    ) {
        Long uid = null;
        if(authentication != null){
            uid = Long.valueOf(authentication.getName());
        }

        return postService.findAll(keyword, type, pageable, uid);
    }

    // 글 상세
    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal String userId
    ) {
        Long uid = userId != null ? Long.valueOf(userId) : null;
        return ResponseEntity.ok(postService.getPostDetail(id, uid));
    }

    // 글 수정
    @PutMapping("/{id}")
    public void update(
            @PathVariable
            Long id,
            @RequestBody
            PostRequest request,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        postService.update(id, userId, request.getTitle(), request.getContent());
    }

    // 글 삭제
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable
            Long id,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        postService.delete(id, userId);
    }

    // 추천 추가
    @PostMapping("/{id}/like")
    public void likePost(
            @PathVariable Long id,
            @AuthenticationPrincipal String userId
    ) {
        postService.likePost(id, Long.valueOf(userId));
    }

    // 추천 취소
    @DeleteMapping("/{id}/like")
    public void unlikePost(
            @PathVariable Long id,
            @AuthenticationPrincipal String userId
    ) {
        postService.unlikePost(id, Long.valueOf(userId));
    }

    // 추천순 정렬
    @GetMapping("/popular")
    public Page<PostResponse> getPopularPosts(
            Pageable pageable,
            @AuthenticationPrincipal String userId
    ){
        Long uid = userId != null ? Long.valueOf(userId) : null;
        return postService.getPopularPosts(pageable, uid);
    }

    // 인기 게시판
    @GetMapping("/hot")
    public Page<PostResponse> getHotPosts(
            @RequestParam(defaultValue = "10") int threshold,
            Pageable pageable,
            @AuthenticationPrincipal String userId
    ){
        Long uid = userId != null ? Long.valueOf(userId) : null;
        return postService.getHotPosts(threshold, pageable, uid);
    }
}