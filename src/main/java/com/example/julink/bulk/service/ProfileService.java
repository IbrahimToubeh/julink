package com.example.julink.bulk.service;

import com.example.julink.bulk.dto.UpdateProfileDto;
import com.example.julink.bulk.dto.UserProfileDto;
import org.springframework.stereotype.Component;

@Component
public interface ProfileService {
   UserProfileDto getProfile(String username);
   UserProfileDto updateProfile(String username, UpdateProfileDto updateDto);

}
