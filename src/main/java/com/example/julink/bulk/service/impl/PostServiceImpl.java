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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
        if (post == null) {
            return null;
        }

        PostDto dto = new PostDto();

        // ID
        dto.setId(post.getId());

        // Author
        if (post.getAuthor() != null) {
            dto.setAuthorId(post.getAuthor().getId());
            dto.setAuthorUsername(post.getAuthor().getUsername());
        }

        // Content
        dto.setContent(post.getContent());

        // Title
        dto.setPostTitle(post.getTitle());

        // Timestamps
        dto.setCreatedAt(post.getCreatedAt());
        dto.setEditedAt(post.getEditedAt());

        // Tagged colleges
        if (post.getTaggedColleges() != null && !post.getTaggedColleges().isEmpty()) {
            dto.setTaggedCollegeIds(
                    post.getTaggedColleges().stream()
                            .filter(Objects::nonNull)
                            .map(College::getId)
                            .toList()
            );
        } else {
            dto.setTaggedCollegeIds(Collections.emptyList());
        }

        // Likes
        dto.setLikeCount(post.getLikes() != null ? post.getLikes().size() : 0);

        // Image
        dto.setImage(post.getImage());

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

    private Post toEntity(PostDto dto) throws IOException {
        if (dto == null) {
            throw new IllegalArgumentException("PostDto cannot be null");
        }

        Post post = new Post();

        // ID
        if (dto.getId() != null) {
            post.setId(dto.getId());
        }

        // Content
        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        } else {
            post.setContent(""); // default empty string if null
        }

        // Title
        if (dto.getPostTitle() != null) {
            post.setTitle(dto.getPostTitle());
        }

        // CreatedAt
        post.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());

        // EditedAt
        post.setEditedAt(dto.getEditedAt());

        // Tagged colleges
        if (dto.getTaggedCollegeIds() != null && !dto.getTaggedCollegeIds().isEmpty()) {
            post.setTaggedColleges(new HashSet<>(collegeRepo.findAllById(dto.getTaggedCollegeIds())));
        } else {
            post.setTaggedColleges(Collections.emptySet());
        }

        // Image
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            post.setImage(dto.getImageFile().getBytes());
        } else if (dto.getImage() != null) {
            post.setImage(dto.getImage()); // allow setting existing byte[] if present
        }

        Users author = null;
        if (dto.getAuthorId() != null) {
            author = userRepo.findById(dto.getAuthorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        } else if (dto.getAuthorUsername() != null) {
            author = userRepo.findByUsername(dto.getAuthorUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        }

        if (author == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author information is required");
        }
        post.setAuthor(author);

        return post;
    }


    @Override
    @Transactional
    public PostDto createPost(PostDto postDto) throws IOException {
        if (postDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post data is required");
        }

        Users author = userRepo.findById(postDto.getAuthorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Post post = toEntity(postDto);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        Post saved = postRepo.save(post);
        return toDto(saved);
    }


    @Override
    @Transactional
    public PostDto editPost(Long postId, PostDto updatedPostDto) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (updatedPostDto.getContent() != null) {
            post.setContent(updatedPostDto.getContent());
        }

        if (updatedPostDto.getPostTitle() != null) {
            post.setTitle(updatedPostDto.getPostTitle());
        }

        if (updatedPostDto.getTaggedCollegeIds() != null) {
            post.setTaggedColleges(new HashSet<>(collegeRepo.findAllById(updatedPostDto.getTaggedCollegeIds())));
        }

        post.setEditedAt(LocalDateTime.now());

        Post saved = postRepo.save(post);
        return toDto(saved);
    }

    @Override
    @Transactional
    public PostDto uploadPostImage(Long postId, MultipartFile file, Long userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this post");
        }

        post.setImage(file.getBytes());
        Post saved = postRepo.save(post);

        return toDto(saved);
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

        if (likeRepo.findByUserAndPost(user, post).isPresent()) {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);

            likeRepo.save(like);

            // update both sides of the relationship
            post.getLikes().add(like);
            // optional: user.getLikes().add(like) if Users has a Set<Like>
        }
    }


    @Override
    @Transactional
    public void removeLike(Long postId, Long userId) {
        // Fetch user
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Fetch post
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Delete like if it exists
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
