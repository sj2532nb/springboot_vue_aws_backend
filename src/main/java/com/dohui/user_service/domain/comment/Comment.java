package com.dohui.user_service.domain.comment;

import com.dohui.user_service.domain.post.dto.entity.Post;
import com.dohui.user_service.domain.user.User;
import com.dohui.user_service.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    public Comment(User user, Post post, String content, String username, Comment parent){
        this.user = user;
        this.post = post;
        this.content = content;
        this.username = username;
        this.parent = parent;
    }

    public void update(String content){
        this.content = content;
    }

    public void softDelete() {
        this.deleted = true;
    }
}
