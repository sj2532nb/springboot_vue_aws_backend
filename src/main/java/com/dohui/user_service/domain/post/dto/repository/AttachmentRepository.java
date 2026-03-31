package com.dohui.user_service.domain.post.dto.repository;

import com.dohui.user_service.domain.post.dto.entity.Attachment;
import com.dohui.user_service.domain.post.dto.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    void deleteByPost(Post post);
}
