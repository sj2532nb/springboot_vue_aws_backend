package com.dohui.user_service.domain.post.dto.repository;

import com.dohui.user_service.domain.post.dto.entity.Post;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecs {
    // 삭제되지 않은 글
    public static Specification<Post> notDeleted(){
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    // 제목에 포함(대소문자 무시)
    public static Specification<Post> titleContains(String keyword){
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    // 내용에 포함(대소문자 무시)
    public static Specification<Post> contentContains(String keyword){
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%");
    }

    // 제목 또는 내용에 포함
    public static Specification<Post> titleOrContentContains(String keyword){
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%")
        );
    }
}
