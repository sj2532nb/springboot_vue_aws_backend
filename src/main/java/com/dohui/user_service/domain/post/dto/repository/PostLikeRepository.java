package com.dohui.user_service.domain.post.dto.repository;

import com.dohui.user_service.domain.post.dto.entity.Post;
import com.dohui.user_service.domain.post.dto.entity.PostLike;
import com.dohui.user_service.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserAndPost(User user, Post post);

    @Modifying
    @Query("delete from PostLike pl where pl.user = :user and pl.post = :post")
    int deleteByUserAndPost(@Param("user") User user, @Param("post") Post post);
    int countByPost(Post post);
}
