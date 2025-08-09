package com.example.julink.bulk.service.impl;

import com.example.julink.bulk.dto.CommentDto;
import com.example.julink.bulk.dto.PostDto;
import com.example.julink.bulk.entity.*;
import com.example.julink.bulk.repositories.CollegeRepository;
import com.example.julink.bulk.repositories.CommentRepo;
import com.example.julink.bulk.repositories.LikeRepository;
import com.example.julink.bulk.repositories.PostRepo;
import com.example.julink.bulk.service.PostService;
import com.example.julink.bulk.service.mapper.CommentMapper;
import com.example.julink.bulk.service.mapper.PostMapper;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;
    private final CommentRepo commentRepo;
    private final CollegeRepository collegeRepo;
    private final LikeRepository likeRepo;
    private final UserRepo userRepo;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    // Mapper methods

    private PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setAuthorId(post.getAuthor().getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setEditedAt(post.getEditedAt());
        dto.setTaggedCollegeIds(post.getTaggedColleges().stream().map(College::getId).toList());
        dto.setLikeCount(likeRepo.countByPost(post));
        dto.setAuthorUsername(post.getAuthor().getUsername());
        return dto;
    }

    private CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setCommenterId(comment.getCommenter().getId());
        dto.setPostId(comment.getPost().getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setEditedAt(comment.getEditedAt());
        return dto;
    }

    private Post toEntity(PostDto dto) {
        Post post = new Post();
        if (dto.getId() != null) post.setId(dto.getId());
        post.setContent(dto.getContent());
        post.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        post.setEditedAt(dto.getEditedAt());
        post.setTaggedColleges(new HashSet<>(collegeRepo.findAllById(dto.getTaggedCollegeIds())));

        // Always load author from DB by ID to avoid trusting client
        Users author = userRepo.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        post.setAuthor(author);

        return post;
    }

    @Override
    @Transactional
    public PostDto createPost(PostDto postDto) {
        Post post = toEntity(postDto);
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(userRepo.findByUsername(postDto.getAuthorUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
        Post saved = postRepo.save(post);
        return toDto(saved);
    }

    @Override
    @Transactional
    public PostDto editPost(Long postId, PostDto updatedPostDto) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthor().getId().equals(updatedPostDto.getAuthorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to edit this post");
        }

        post.setContent(updatedPostDto.getContent());
        post.setEditedAt(LocalDateTime.now());
        post.setTaggedColleges(new HashSet<>(collegeRepo.findAllById(updatedPostDto.getTaggedCollegeIds())));

        Post updated = postRepo.save(post);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to delete this post");
        }

        postRepo.delete(post);
    }

    @Override
    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Users commenter = userRepo.findById(commentDto.getCommenterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Comment comment = new Comment();
        comment.setCommenter(commenter);
        comment.setPost(post);
        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepo.save(comment);
        return toDto(saved);
    }

    @Override
    @Transactional
    public CommentDto editComment(Long commentId, CommentDto updatedCommentDto) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getCommenter().getId().equals(updatedCommentDto.getCommenterId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to edit this comment");
        }

        comment.setContent(updatedCommentDto.getContent());
        comment.setEditedAt(LocalDateTime.now());

        Comment updated = commentRepo.save(comment);
        return toDto(updated);
    }


    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepo.getCommentsByPostId(postId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void addLike(Long postId, Long userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (likeRepo.findByUserAndPost(user, post).isEmpty()) {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepo.save(like);
        }
    }

    @Override
    @Transactional
    public void removeLike(Long postId, Long userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        likeRepo.findByUserAndPost(user, post)
                .ifPresent(likeRepo::delete);
    }

    @Override
    public Page<PostDto> getHomepagePosts(Long userCollegeId, Pageable pageable) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        Page<Post> postsPage = postRepo.findRecentPostsByCollegeSorted(userCollegeId, oneWeekAgo, pageable);
        return postsPage.map(this::toDto);
    }

    @Override
    public Page<PostDto> getPosts(Long collegeId, Long authorId, PageRequest of) {
        if (collegeId != null) {
            return postRepo.findByTaggedCollegeId(collegeId, of)
                    .map(postMapper::toDto);
        }
        if (authorId != null) {
            return postRepo.findByAuthorId(authorId, of)
                    .map(postMapper::toDto);
        }
        return postRepo.findAll(of)
                .map(postMapper::toDto);
    }





}
