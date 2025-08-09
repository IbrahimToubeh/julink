package com.example.julink.bulk.service.impl;

import com.example.julink.bulk.repositories.CollegeRepository;
import com.example.julink.bulk.service.mapper.UserMapper;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.repo.UserRepo;
import com.example.julink.bulk.dto.UpdateProfileDto;
import com.example.julink.bulk.dto.UserProfileDto;
import com.example.julink.bulk.repositories.CommentRepo;
import com.example.julink.bulk.repositories.PostRepo;
import com.example.julink.bulk.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final CollegeRepository collegeRepo;
    public UserProfileDto getProfile(String username) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return userMapper.toDto(user);
    }

    public UserProfileDto updateProfile(String username, UpdateProfileDto dto) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setMajor(dto.getMajor());
        user.setCollege(collegeRepo.getById(dto.getCollegeId()));

        userRepo.save(user);
        return userMapper.toDto(user);
    }
}