package com.dohui.user_service.domain.post.controller;

import com.dohui.user_service.domain.post.service.PostService;
import com.dohui.user_service.domain.post.dto.request.PostRequest;
import com.dohui.user_service.domain.post.dto.response.PostDetailResponse;
import com.dohui.user_service.domain.post.dto.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    // 글 작성
    @PostMapping
    public Long create(
            @RequestBody PostRequest request,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return postService.create(
                userId,
                request.getTitle(),
                request.getContent(),
                request.getFileNames(),
                request.getFileUrls(),
                request.isPrivate()
        );
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
        Long uid = (userId != null && !userId.equals("anonymousUser")) ? Long.valueOf(userId) : null;
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
        postService.update(
                id,
                userId,
                request.getTitle(),
                request.getContent(),
                request.getFileNames(),
                request.getFileUrls()
        );
    }

    // 글 삭제
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        postService.delete(id, userId, authentication);
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
        Long uid = (userId != null && !userId.equals("anonymousUser")) ? Long.valueOf(userId) : null;
        return postService.getPopularPosts(pageable, uid);
    }

    // 인기 게시판
    @GetMapping("/hot")
    public Page<PostResponse> getHotPosts(
            @RequestParam(defaultValue = "10") int threshold,
            Pageable pageable,
            @AuthenticationPrincipal String userId
    ){
        Long uid = (userId != null && !userId.equals("anonymousUser")) ? Long.valueOf(userId) : null;
        return postService.getHotPosts(threshold, pageable, uid);
    }

    // 내가 쓴 글 목록
    @GetMapping("/me")
    public Page<PostResponse> getMyPosts(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication
    ){
        // 비로그인 차단
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Long userId = Long.valueOf(authentication.getName());
        return postService.getMyPosts(userId, pageable);
    }

    // 이전 글 목록
    @GetMapping("/{id}/previous")
    public List<PostResponse> getPreviousPosts(
            @PathVariable("id") Long postId,
            @RequestParam(required = false) String from,
            Authentication authentication
    ){
        Long userId = null;
        if (authentication != null && !authentication.getName().equals("anonymousUser")){
            userId = Long.valueOf(authentication.getName());
        }
        return postService.getPreviousPosts(postId, userId, from);
    }

    // 비공개 글 목록
    @GetMapping("/private")
    public Page<PostResponse> getPrivatePosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Long userId = Long.valueOf(authentication.getName());
        return postService.getPrivatePosts(userId, pageable);
    }

    // 이전 글, 다음 글 버튼
    @GetMapping("/{id}/adjacent/private")
    public Map<String, Long> getPrivateAdjacentPosts(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return postService.getPrivateAdjacentPosts(id, userId);
    }
}