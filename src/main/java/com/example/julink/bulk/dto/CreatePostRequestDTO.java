package com.example.julink.bulk.dto;

import java.util.List;

public record CreatePostRequestDTO(String content, List<Long> collegeIds
) {}
