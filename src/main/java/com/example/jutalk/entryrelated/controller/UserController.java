package com.example.jutalk.entryrelated.controller;


import com.example.jutalk.entryrelated.Users;
import com.example.jutalk.entryrelated.dto.CreateUserRequestDTO;
import com.example.jutalk.entryrelated.dto.LoginUserRequestDTO;
import com.example.jutalk.entryrelated.repo.UserRepo;
import com.example.jutalk.entryrelated.service.UserService;
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
        System.out.println("""
---- LOGIN ATTEMPT ----
Username : %s
Raw Pass  : %s
Stored Hash matches? %s
-----------------------
""".formatted(
                loginUserRequestDTO.username(),
                loginUserRequestDTO.password(),
                passwordEncoder.matches(
                        loginUserRequestDTO.password(),
                        userRepo.findByUsername(loginUserRequestDTO.username())
                                .map(Users::getPassword)
                                .orElse("NO USER")
                )
        ));

        return userService.authenticate(loginUserRequestDTO);
    }
}
