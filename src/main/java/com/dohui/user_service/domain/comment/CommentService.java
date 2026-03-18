package com.dohui.user_service.domain.comment;

import com.dohui.user_service.domain.comment.dto.response.CommentPageResponse;
import com.dohui.user_service.domain.comment.dto.response.CommentResponse;
import com.dohui.user_service.domain.post.dto.entity.Post;
import com.dohui.user_service.domain.post.dto.repository.PostRepository;
import com.dohui.user_service.domain.user.User;
import com.dohui.user_service.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 댓글 작성
    public Long create(Long userId, Long postId, String content, Long parentId){
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("유저 없음"));
        Post post = postRepository.findById(postId).orElseThrow(()-> new IllegalArgumentException("게시글 없음"));
        Comment parent = null;
        if(parentId != null){
            parent = commentRepository.findById(parentId).orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
            if (!parent.getPost().getId().equals(postId)){
                throw new IllegalArgumentException("다른 게시글의 댓글에는 대댓글을 작성할 수 없습니다");
            }
            if (parent.getParent() != null){
                throw new IllegalArgumentException("대댓글에는 댓글을 작성할 수 없습니다");
            }
            if (parent.isDeleted()){
                throw new IllegalArgumentException("삭제된 댓글에는 대댓글을 작성할 수 없습니다");
            }
        }
        Comment comment = new Comment(user, post, content, parent);
        commentRepository.save(comment);
        post.increaseCommentCount();

        return comment.getId();
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public CommentPageResponse findByPost(Long postId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        // 부모 댓글 페이징
        Page<Comment> parentPage = commentRepository.findByPostIdAndParentIsNull(postId, pageable);
        List<Comment> parents = parentPage.getContent();
        // 부모 id 리스트
        List<Long> parentIds = parents.stream().map(Comment::getId).toList();
        // 대댓글 조회
        List<Comment> children = parentIds.isEmpty() ? List.of() : commentRepository.findByParentIdInOrderByIdAsc(parentIds);
        // parentId -> children map
        Map<Long, List<Comment>> childMap = children.stream().collect(Collectors.groupingBy(c -> c.getParent().getId()));
        // DTO 변환
        List<CommentResponse> result = parents.stream().map(parent -> {
            CommentResponse parentDto = CommentResponse.from(parent);
            List<CommentResponse> replies = childMap.getOrDefault(parent.getId(), List.of()).stream()
                    .map(CommentResponse::from)
                    .toList();
            parentDto.getReplies().addAll(replies);

            return parentDto;
        }).toList();

        return new CommentPageResponse(
                result,
                parentPage.getTotalPages(),
                parentPage.getTotalElements()
        );
    }

    // 댓글 수정
    public Long update(Long userId, Long commentId, String content){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new IllegalArgumentException("댓글이 존재하지 않습니다"));
        // 작성자 검증
        if(!comment.getUser().getId().equals(userId)){
            throw new AccessDeniedException("권한이 없습니다");
        }
        comment.update(content);  // 엔티티 메서드로 수정

        return comment.getId();
    }

    // 댓글 삭제
    public Long delete(Long userId, Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new IllegalArgumentException("댓글이 존재하지 않습니다"));
        // 작성자 검증
        if(!comment.getUser().getId().equals(userId)){
            throw new AccessDeniedException("권한이 없습니다");
        }
        // 이미 삭제된 댓글이면 아무것도 안함
        if(!comment.isDeleted()){
            comment.softDelete();
            comment.getPost().decreaseCommentCount();
        }

        return commentId;
    }
}
