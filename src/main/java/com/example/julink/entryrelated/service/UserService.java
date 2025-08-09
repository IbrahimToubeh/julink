package com.example.julink.entryrelated.service;

import com.example.julink.entryrelated.dto.CreateUserRequestDTO;
import com.example.julink.entryrelated.dto.LoginUserRequestDTO;
import com.example.julink.entryrelated.dto.SendOTPRequestDTO;
import org.springframework.stereotype.Component;

@Component
public interface UserService {
    void createUser(CreateUserRequestDTO createUserRequestDTO);
    String authenticate(LoginUserRequestDTO loginUserRequestDTO);

    void sendOTP(String email);
    void checkOTPValidity(String email,String otp);

    void resetPasswordAfterOTP(String email, String newPassword);

    void changePassword(String username, String currentPassword, String newPassword);

}
