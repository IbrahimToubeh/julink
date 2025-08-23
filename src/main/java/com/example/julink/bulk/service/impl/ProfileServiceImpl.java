package com.example.julink.bulk.service.impl;

import com.example.julink.bulk.dto.UpdateProfileDto;
import com.example.julink.bulk.dto.UserProfileDto;
import com.example.julink.bulk.service.ProfileService;
import com.example.julink.bulk.entity.College;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.repo.UserRepo;
import com.example.julink.bulk.repositories.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepo userRepo;
    private final CollegeRepository collegeRepo;

    @Override
    public UserProfileDto getProfile(String username) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserProfileDto updateProfile(String username, UpdateProfileDto updateDto) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (updateDto.getFirstName() != null) user.setFirstName(updateDto.getFirstName());
        if (updateDto.getLastName() != null) user.setLastName(updateDto.getLastName());
        if (updateDto.getMajor() != null) user.setMajor(updateDto.getMajor());

        if (updateDto.getCollegeId() != null) {
            College college = collegeRepo.findById(updateDto.getCollegeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "College not found"));
            user.setCollege(college);
        }

        userRepo.save(user);
        return mapToDto(user);
    }

    @Override
    @Transactional
    public void deactivateAccount(Long userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setActive(false);
        user.setDeactivatedAt(LocalDateTime.now());
        userRepo.save(user);
    }




    @Override
    @Transactional
    public void deleteProfileImage(long userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setProfileImage(null);
        userRepo.save(user);
    }

    private UserProfileDto mapToDto(Users user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setMajor(user.getMajor());
        dto.setCollegeId(user.getCollege() != null ? user.getCollege().getId() : null);
        dto.setProfileImage(user.getProfileImage());
        dto.setFollowingIds(user.getFollowing().stream().map(Users::getId).toList());
        dto.setFollowerIds(user.getFollowers().stream().map(Users::getId).toList());
        return dto;
    }
}
