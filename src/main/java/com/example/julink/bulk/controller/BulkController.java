package com.example.julink.bulk.controller;

import com.example.julink.bulk.dto.CommentDto;
import com.example.julink.bulk.dto.PostDto;
import com.example.julink.bulk.dto.UpdateProfileDto;
import com.example.julink.bulk.dto.UserProfileDto;
import com.example.julink.bulk.service.PostService;
import com.example.julink.bulk.service.ProfileService;
import com.example.julink.config.UserPrincipal;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BulkController {

    private final PostService postService;
    private final ProfileService profileService;
    private final UserRepo userRepo;

    // --- Profile ---

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(profileService.getProfile(userPrincipal.getUsername()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(@RequestBody UpdateProfileDto updatedProfile,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(profileService.updateProfile(userPrincipal.getUsername(), updatedProfile));
    }

    @GetMapping("/profile/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Users user = userRepo.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        byte[] image = user.getProfileImage();
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @DeleteMapping("/profile/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = getUserId(userPrincipal);

        try {
            profileService.deleteProfileImage(userId);
        } catch (ResponseStatusException e) {
            // Return 404 if user/profile not found
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/profile/deactivate")
    public ResponseEntity<Void> deactivateAccount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        profileService.deactivateAccount(getUserId(userPrincipal));
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{followeeId}/follow")
    public ResponseEntity<Void> followUser(@PathVariable Long followeeId,
                                           @RequestParam Long followerId) {
        postService.followUser(followerId, followeeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{followeeId}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long followeeId,
                                             @RequestParam Long followerId) {
        postService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<Set<Users>> getFollowing(@PathVariable Long userId) {
        Set<Users> following = postService.getFollowingList(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Set<Users>> getFollowers(@PathVariable Long userId) {
        Set<Users> followers = postService.getFollowersList(userId);
        return ResponseEntity.ok(followers);
    }

    // --- Posts ---

    @PostMapping("/posts")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) throws IOException {
        postDto.setAuthorId(getUserId(userPrincipal));
        PostDto created = postService.createPost(postDto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/posts/{postId}/image")
    public ResponseEntity<PostDto> uploadPostImage(@PathVariable Long postId,
                                                   @RequestParam("file") MultipartFile file,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) throws IOException {
        PostDto updated = postService.uploadPostImage(postId, file, getUserId(userPrincipal));
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDto> editPost(@PathVariable Long postId,
                                            @RequestBody PostDto postDto,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        postDto.setAuthorId(getUserId(userPrincipal));
        PostDto updated = postService.editPost(postId, postDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        postService.deletePost(postId, getUserId(userPrincipal));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostDto>> getPosts(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) Long collegeId,
                                                  @RequestParam(required = false) Long authorId) {
        Page<PostDto> posts = postService.getPosts(collegeId, authorId, PageRequest.of(page, size));
        return ResponseEntity.ok(posts);
    }

    // --- Comments ---

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId,
                                                    @RequestBody CommentDto commentDto,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        commentDto.setCommenterId(getUserId(userPrincipal));
        CommentDto created = postService.createComment(postId, commentDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> editComment(@PathVariable Long commentId,
                                                  @RequestBody CommentDto commentDto,
                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        commentDto.setCommenterId(getUserId(userPrincipal));
        CommentDto updated = postService.editComment(commentId, commentDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        postService.deleteComment(commentId, getUserId(userPrincipal));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentDto> comments = postService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // --- Likes ---

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> addLike(@PathVariable Long postId,
                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        postService.addLike(postId, getUserId(userPrincipal));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> removeLike(@PathVariable Long postId,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        postService.removeLike(postId, getUserId(userPrincipal));
        return ResponseEntity.noContent().build();
    }

    // --- Homepage Posts ---

    @GetMapping("/posts/homepage")
    public ResponseEntity<Page<PostDto>> getHomepagePosts(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userCollegeId = getUserCollegeId(userPrincipal);
        Page<PostDto> posts = postService.getHomepagePosts(userCollegeId, PageRequest.of(page, size));
        return ResponseEntity.ok(posts);
    }




    // --- Helper methods ---

    private Long getUserId(UserPrincipal userPrincipal) {
        return userRepo.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .getId();
    }

    private Long getUserCollegeId(UserPrincipal userPrincipal) {
        Users user = userRepo.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getCollege() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User's college not set");
        }
        return user.getCollege().getId();
    }
}
