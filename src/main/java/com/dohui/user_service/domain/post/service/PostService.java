package com.dohui.user_service.domain.post.service;

import com.dohui.user_service.domain.comment.CommentRepository;
import com.dohui.user_service.domain.post.dto.entity.Attachment;
import com.dohui.user_service.domain.post.dto.entity.Post;
import com.dohui.user_service.domain.post.dto.entity.PostLike;
import com.dohui.user_service.domain.post.dto.repository.AttachmentRepository;
import com.dohui.user_service.domain.post.dto.repository.PostLikeRepository;
import com.dohui.user_service.domain.post.dto.repository.PostRepository;
import com.dohui.user_service.domain.post.dto.repository.PostSpecs;
import com.dohui.user_service.domain.post.dto.response.PostDetailResponse;
import com.dohui.user_service.domain.post.dto.response.PostResponse;
import com.dohui.user_service.domain.user.User;
import com.dohui.user_service.domain.user.UserRepository;
import com.dohui.user_service.global.exception.AlreadyLikedException;
import com.dohui.user_service.global.exception.LikeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final AttachmentRepository attachmentRepository;

    // 글 작성
    public Long create(Long userId, String title, String content, List<String> fileNames, List<String> fileUrls) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Post post = Post.builder()
                .user(user)
                .title(title)
                .content(content)
                .username(user.getNickname())
                .build();

        postRepository.save(post);

        if (fileUrls != null && fileNames != null) {

            if (fileUrls.size() != fileNames.size()) {
                throw new IllegalArgumentException("파일 정보 불일치");
            }

            for (int i = 0; i < fileUrls.size(); i++) {
                Attachment attachment = new Attachment(
                        fileNames.get(i),
                        null,
                        fileUrls.get(i),
                        post
                );
                attachmentRepository.save(attachment);
            }
        }

        return post.getId();
    }

    // 글 목록
    @Transactional(readOnly = true)
    public Page<PostResponse> findAll(String keyword, String type, Pageable pageable, Long userId) {

        Specification<Post> spec = PostSpecs.notDeleted();

        if (keyword != null && !keyword.isBlank()) {
            String[] keywords = keyword.trim().split("\\s+");

            for (String word : keywords) {
                spec = spec.and(
                        switch (type) {
                            case "title" -> PostSpecs.titleContains(word);
                            case "content" -> PostSpecs.contentContains(word);
                            default -> PostSpecs.titleOrContentContains(word);
                        }
                );
            }
        }

        Page<Post> page = postRepository.findAll(spec, pageable);

        return page.map(post -> PostResponse.from(post, userId));
    }

    // 글 상세
    public PostDetailResponse getPostDetail(Long postId, Long userId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 글입니다"));
        post.increaseViewCount();  // 조회수 증가

        boolean liked = false;
        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 유저입니다"));
            liked = postLikeRepository.existsByUserAndPost(user, post);
        }
        boolean author = userId != null && post.getUser().getId().equals(userId);

        return PostDetailResponse.from(post, post.getLikeCount(), liked, author);
    }

    // 글 수정(권한 체크)
    public void update(Long postId, Long userId, String title, String content, List<String> fileNames, List<String> fileUrls) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("수정 권한 없음");
        }

        post.update(title, content);

        attachmentRepository.deleteByPost(post);

        if (fileUrls != null && fileNames != null) {

            if (fileUrls.size() != fileNames.size()) {
                throw new IllegalArgumentException("파일 정보 불일치");
            }

            for (int i = 0; i < fileUrls.size(); i++) {
                Attachment attachment = new Attachment(
                        fileNames.get(i),
                        null,
                        fileUrls.get(i),
                        post
                );
                attachmentRepository.save(attachment);
            }
        }
    }

    // 글 삭제(권한 체크)
    public void delete(Long postId, Long userId, Authentication authentication) {
        Post post = postRepository.findByIdAndDeletedFalse(postId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 글입니다"));

        boolean isOwner = post.getUser().getId().equals(userId);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("삭제 권한이 없습니다");
        }
        post.softDelete();
    }

    // 추천 추가
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 글입니다"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 유저입니다"));
        // 1차 방어(가독성 + 빠른 예외처리)
        if(postLikeRepository.existsByUserAndPost(user, post)){
            throw new AlreadyLikedException();
        }
        // 2차 방어(동시성 대비)
        try{
            postLikeRepository.save(new PostLike(user, post));
            post.increaseLikeCount();
        }
        catch (DataIntegrityViolationException e){
            throw new AlreadyLikedException();  // 동시 요청이 먼저 들어간 경우
        }
    }

    // 추천 취소
    public void unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 글입니다"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 유저입니다"));
        int deleted = postLikeRepository.deleteByUserAndPost(user, post);
        if(deleted == 0){
            throw new LikeNotFoundException();  // 추천 취소할 게 없을때
        }
        post.decreaseLikeCount();
    }

    // 추천순 정렬
    @Transactional(readOnly = true)
    public Page<PostResponse> getPopularPosts(Pageable pageable, Long userId){
        return postRepository.findByDeletedFalseOrderByLikeCountDescCreatedAtDesc(pageable)
                .map(post -> PostResponse.from(post, userId));
    }

    // 인기 게시판
    @Transactional(readOnly = true)
    public Page<PostResponse> getHotPosts(int threshold, Pageable pageable, Long userId){
        return postRepository.findByDeletedFalseAndLikeCountGreaterThanEqualOrderByCreatedAtDesc(threshold, pageable)
                .map(post -> PostResponse.from(post, userId));
    }

    // 내가 쓴 글 목록
    @Transactional(readOnly = true)
    public Page<PostResponse> getMyPosts(Long userId, Pageable pageable){
        return postRepository.findByUserIdAndDeletedFalse(userId, pageable).map(post -> PostResponse.from(post, userId));
    }

    // 이전 글 목록
    @Transactional(readOnly = true)
    public List<PostResponse> getPreviousPosts(Long postId, Long userId, String from){
        List<Post> posts;
        if("hot".equals(from)){
            posts = postRepository.findPreviousHotPosts(postId, 10);
        }
        else{
            posts = postRepository.findTop5ByIdLessThanAndDeletedFalseOrderByIdDesc(postId);
        }
        return posts.stream().map(post -> PostResponse.from(post, userId)).toList();
    }
}