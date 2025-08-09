package com.example.julink.bulk.repositories;

import com.example.julink.bulk.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findByCommenterId(Long commenterId);
    List<Comment> getCommentsByPostId(Long postId);

}
