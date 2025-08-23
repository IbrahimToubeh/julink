package com.example.julink.bulk.service;

import com.example.julink.bulk.dto.CommentDto;
import com.example.julink.bulk.dto.PostDto;
import com.example.julink.bulk.dto.UserMiniDto;
import com.example.julink.entryrelated.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface PostService {

    PostDto createPost(PostDto postDto) throws IOException;
    PostDto editPost(Long postId, PostDto updatedPostDto);
    void deletePost(Long postId, Long userId);
    PostDto uploadPostImage(Long postId, MultipartFile file, Long userId) throws IOException;

    CommentDto createComment(Long postId, CommentDto commentDto);
    CommentDto editComment(Long commentId, CommentDto updatedCommentDto);
    void deleteComment(Long commentId, Long userId);

    void addLike(Long postId, Long userId);
    void removeLike(Long postId, Long userId);

    Page<PostDto> getHomepagePosts(Long userCollegeId, Pageable pageable);
    Page<PostDto> getPosts(Long collegeId, Long authorId, PageRequest of);
    List<CommentDto> getCommentsByPostId(Long postId);
    void followUser(Long followerId, Long followeeId);
    void unfollowUser(Long followerId, Long followeeId);
    Set<UserMiniDto> getFollowingList(Long userId);
    Set<UserMiniDto> getFollowersList(Long userId);

    Page<PostDto> getPostsLikedByUser(Long userId, Pageable pageable);
    Page<PostDto> getPostsCommentedByUser(Long userId, Pageable pageable);




}
