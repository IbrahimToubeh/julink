package com.example.julink.bulk.service;

import com.example.julink.bulk.dto.CommentDto;
import com.example.julink.bulk.dto.PostDto;
import com.example.julink.bulk.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    PostDto createPost(PostDto postDto);
    PostDto editPost(Long postId, PostDto updatedPostDto);
    void deletePost(Long postId, Long userId);

    CommentDto createComment(Long postId, CommentDto commentDto);
    CommentDto editComment(Long commentId, CommentDto updatedCommentDto);


    void addLike(Long postId, Long userId);
    void removeLike(Long postId, Long userId);

    Page<PostDto> getHomepagePosts(Long userCollegeId, Pageable pageable);

    Page<PostDto> getPosts(Long collegeId, Long authorId, PageRequest of);

    List<CommentDto> getCommentsByPostId(Long postId);

}
