package com.example.julink.bulk.dto;



import lombok.Data;

@Data
public class UpdateProfileDto {
    private String firstName;
    private String lastName;
    private String major;
    private Long collegeId;
}
