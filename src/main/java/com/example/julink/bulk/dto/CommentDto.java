package com.example.julink.bulk.dto;



import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long commenterId;
    private String commenterUsername;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
}