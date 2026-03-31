package com.dohui.user_service.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 부모 댓글 페이징
    Page<Comment> findByPostIdAndParentIsNull(Long postId, Pageable pageable);
    // 대댓글 조회
    List<Comment> findByParentIdInOrderByIdAsc(List<Long> parentIds);
    // 내가 작성한 댓글 조회
    Page<Comment> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);
}