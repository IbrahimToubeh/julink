package com.example.julink.bulk.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserMiniDto {
    private Long id;
    private String username;
    private byte[] profilePicture;
}
