package com.dohui.user_service.domain.post.dto.repository;

import com.dohui.user_service.domain.post.dto.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

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
    Page<Post> findByDeletedFalseOrderByLikeCountDescCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Post> findByDeletedFalseAndLikeCountGreaterThanEqualOrderByCreatedAtDesc(
            int likeCount,
            Pageable pageable
    );
}
