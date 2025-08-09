package com.example.julink.bulk.dto;

public record ChangePasswordRequestDTO(
        String currentPassword,
        String newPassword
) {}