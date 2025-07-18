package com.example.jutalk.entryrelated.service;

import com.example.jutalk.entryrelated.dto.CreateUserRequestDTO;
import com.example.jutalk.entryrelated.dto.LoginUserRequestDTO;
import org.springframework.stereotype.Component;

@Component
public interface UserService {
    void createUser(CreateUserRequestDTO createUserRequestDTO);
    String authenticate(LoginUserRequestDTO loginUserRequestDTO);
}
