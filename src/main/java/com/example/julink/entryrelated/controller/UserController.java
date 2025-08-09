package com.example.julink.entryrelated.controller;


import com.example.julink.config.UserPrincipal;
import com.example.julink.entryrelated.dto.CheckOTPValidityRequestDTO;
import com.example.julink.entryrelated.dto.CreateUserRequestDTO;
import com.example.julink.entryrelated.dto.LoginUserRequestDTO;
import com.example.julink.entryrelated.dto.SendOTPRequestDTO;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.repo.UserRepo;
import com.example.julink.entryrelated.service.UserService;
import com.example.julink.bulk.dto.ChangePasswordRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/entry")
public class UserController {

    private final UserService userService;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/createUser")
    public String createUser(@RequestBody CreateUserRequestDTO createUserRequestDTO) {
        userService.createUser(createUserRequestDTO);
        return "success";
    }
    @PostMapping("/loginUser")
    public String loginUser(@RequestBody LoginUserRequestDTO loginUserRequestDTO) {

        return userService.authenticate(loginUserRequestDTO);
    }

    @PostMapping("/sendOTP")
    public String sendOTP(@RequestBody SendOTPRequestDTO sendOTPRequestDTo){
        userService.sendOTP(sendOTPRequestDTo.email());
        return "success";
    }

    @PostMapping("/checkOTPValidity")
    public String checkOTPValidity(@RequestBody CheckOTPValidityRequestDTO checkOTPValidityRequestDTO){
        userService.checkOTPValidity(checkOTPValidityRequestDTO.otp(),checkOTPValidityRequestDTO.email());
        return "success";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestBody SendOTPRequestDTO request) {
        userService.sendOTP(request.email());
        return "OTP sent to email";
    }

    @PostMapping("/checkForgotPasswordOTP")
    public String checkForgotPasswordOTP(@RequestBody CheckOTPValidityRequestDTO request) {
        userService.checkOTPValidity(request.email(), request.otp());
        return "OTP is valid";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String newPassword) {
        userService.resetPasswordAfterOTP(email, newPassword);
        return "Password reset successfully";
    }


    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ChangePasswordRequestDTO request
    ) {
        userService.changePassword(userPrincipal.getUsername(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok("Password changed successfully.");
    }
    @PostMapping("/upload-image-test")
    public ResponseEntity<String> test() {
        System.out.println("Upload-image-test hit");
        return ResponseEntity.ok("Test endpoint reached");
    }


    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) throws IOException {

        Users user = userRepo.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println("Uploading image for user: " + user.getUsername() + " File size: " + file.getSize());

        user.setProfileImage(file.getBytes());
        userRepo.save(user);

        System.out.println("User saved with image length: " + (user.getProfileImage() == null ? 0 : user.getProfileImage().length));


        return ResponseEntity.ok("Image uploaded successfully.");
    }



}
