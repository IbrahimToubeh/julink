package com.example.julink.entryrelated.service;

import com.example.julink.config.JWTService;
import com.example.julink.entryrelated.dto.OTPDTO;
import com.example.julink.entryrelated.entity.OTPClass;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.dto.CreateUserRequestDTO;
import com.example.julink.entryrelated.dto.LoginUserRequestDTO;
import com.example.julink.entryrelated.repo.OTPRepository;
import com.example.julink.entryrelated.repo.UserRepo;
import com.example.julink.entryrelated.service.mapper.UserRelatedMapstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserRelatedMapstruct userRelatedMapstruct;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JavaMailSender javaMailSender;
    private final OTPRepository otpRepository;
    @Override
    public void createUser(CreateUserRequestDTO createUserRequestDTO) {
        Users user = userRelatedMapstruct.CreateUserRequestsDTOtoEntity(createUserRequestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }

    @Override
    public String authenticate(LoginUserRequestDTO loginUserRequestDTO) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequestDTO.username(),loginUserRequestDTO.password())
        );
        if (auth.isAuthenticated()) {
            Users user = (Users) auth.getPrincipal();
            if (!user.isActive()){
                user.setActive(true);
                user.setDeactivatedAt(null);
            }
            return jwtService.generateToken((UserDetails) auth.getPrincipal());
        }
        throw new RuntimeException("Invalid username or password");
    }

    //todo: ask about this
    @Override
    public void sendOTP(String email) {
        String otp = otpGenerator();
        sendEmail(email,otp);
        OTPDTO otpdto = new OTPDTO(email,otp);
        OTPClass otpClass = userRelatedMapstruct.OTPDTOtoEntity(otpdto);
        otpClass.setExpires(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpClass);
    }

    @Override
    public void checkOTPValidity(String email, String otp) {
        OTPClass otpEntity = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No OTP found for this email"));

        if (!otpEntity.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP, the given otp is incorrect");
        }

        if (otpEntity.getExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid OTP, the expiration date is after the current date");
        }
        System.out.println("Valid OTP");
    }

    private void sendEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("julinkemailsender@gmail.com");
        message.setTo(email);
        message.setSubject("OTP For JUlink account");
        message.setText("Hello, this is your otp: "+otp +" for JUlink account");
        javaMailSender.send(message);
    }
    private String otpGenerator() {
        return String.valueOf(100000 + (int)(Math.random() * 900000));
    }

    @Override
    public void resetPasswordAfterOTP(String email, String newPassword) {
        OTPClass otp = otpRepository.findByEmail(email)
                .orElseThrow( () -> new RuntimeException("No OTP found for this email")  );



        if (!otp.isVerified()) {
            throw new RuntimeException("OTP not verified. Please verify it first.");
        }

        Users user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        otp.setVerified(false);
        otpRepository.save(otp);
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }


}
