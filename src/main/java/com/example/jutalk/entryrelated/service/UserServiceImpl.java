package com.example.jutalk.entryrelated.service;

import com.example.jutalk.config.JWTService;
import com.example.jutalk.entryrelated.Users;
import com.example.jutalk.entryrelated.dto.CreateUserRequestDTO;
import com.example.jutalk.entryrelated.dto.LoginUserRequestDTO;
import com.example.jutalk.entryrelated.repo.UserRepo;
import com.example.jutalk.entryrelated.service.mapper.CreateUserMapStruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final CreateUserMapStruct createUserMapStruct;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    @Override
    public void createUser(CreateUserRequestDTO createUserRequestDTO) {
        Users user = createUserMapStruct.CreateUserRequestsDTOtoEntity(createUserRequestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }

    @Override
    public String authenticate(LoginUserRequestDTO loginUserRequestDTO) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequestDTO.username(),loginUserRequestDTO.password())
        );
        if (auth.isAuthenticated()) {
            return jwtService.generateToken((UserDetails) auth.getPrincipal());
        }
        throw new RuntimeException("Invalid username or password");
    }


    public void resetUserPassword(String email, String newPassword) {
        Users user = userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    public Users getUserByEmail(String email) {
        return userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void registerUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }
}
