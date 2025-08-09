package com.example.julink.bulk.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private Long id;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private List<Long> taggedCollegeIds;
    private String authorUsername;
    private long likeCount;
    private byte[] image;
    private String postTitle;
    private MultipartFile imageFile;
}