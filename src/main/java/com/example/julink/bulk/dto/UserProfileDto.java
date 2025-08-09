package com.example.julink.bulk.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String major;

    private Long collegeId;
    private String collegeName;

    private String role;
    private byte[] profileImage;

    private List<PostDto> posts;
    private List<CommentDto> comments;
}
