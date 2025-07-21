package com.example.julink.entryrelated.controller;


import com.example.julink.entryrelated.dto.CheckOTPValidityRequestDTO;
import com.example.julink.entryrelated.dto.CreateUserRequestDTO;
import com.example.julink.entryrelated.dto.LoginUserRequestDTO;
import com.example.julink.entryrelated.dto.SendOTPRequestDTO;
import com.example.julink.entryrelated.repo.UserRepo;
import com.example.julink.entryrelated.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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


}
