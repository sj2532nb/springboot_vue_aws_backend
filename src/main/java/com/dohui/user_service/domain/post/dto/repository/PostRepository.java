package com.dohui.user_service.domain.post.dto.repository;

import com.dohui.user_service.domain.post.dto.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    @EntityGraph(attributePaths = "user")
    Page<Post> findByDeletedFalse(Pageable pageable);

    Optional<Post> findByIdAndDeletedFalse(Long id);

    @EntityGraph(attributePaths = "user")
    Page<Post> findByTitleContainingIgnoreCaseAndDeletedFalse(String keyword, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Post> findByContentContainingIgnoreCaseAndDeletedFalse(String keyword, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDeletedFalse(
            String titleKeyword,
            String contentKeyword,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "user")
    Page<Post> findByDeletedFalseAndIsPrivateFalseOrderByLikeCountDescCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Post> findByDeletedFalseAndIsPrivateFalseAndLikeCountGreaterThanEqualOrderByCreatedAtDesc(
            int likeCount,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "user")
    Page<Post> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    // 이전 글 목록
    @EntityGraph(attributePaths = "user")
    List<Post> findTop5ByIdLessThanAndDeletedFalseAndIsPrivateFalseOrderByIdDesc(Long postId);

    // 인기글 이전 글 목록
    @Query("""
            SELECT p FROM Post p
            WHERE p.id < :postId
            AND p.deleted = false
            AND p.isPrivate = false
            AND p.likeCount >= :threshold
            ORDER BY p.id DESC
            """)
    List<Post> findPreviousHotPosts(
            @Param("postId") Long postId,
            @Param("threshold") int threshold
    );

    // 비공개 글 목록
    @EntityGraph(attributePaths = "user")
    Page<Post> findByUserIdAndIsPrivateTrueAndDeletedFalse(Long userId, Pageable pageable);

    // 비공개 글 이전 글 목록
    @Query("""
            SELECT p FROM Post p
            WHERE p.id < :postId
            AND p.deleted = false
            AND p.isPrivate = true
            AND p.user.id >= :userId
            ORDER BY p.id DESC
            LIMIT 5
            """)
    List<Post> findPreviousPrivatePosts(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    // 이전 글 (id가 작은 것 중 가장 큰 것)
    Optional<Post> findFirstByUserIdAndIsPrivateTrueAndDeletedFalseAndIdLessThanOrderByIdDesc(Long userId, Long id);

    // 다음 글 (id가 큰 것 중 가장 작은 것)
    Optional<Post> findFirstByUserIdAndIsPrivateTrueAndDeletedFalseAndIdGreaterThanOrderByIdAsc(Long userId, Long id);
}
