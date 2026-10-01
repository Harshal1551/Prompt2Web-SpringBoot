package com.prompt2web.service;

import com.prompt2web.dto.LoginRequest;
import com.prompt2web.dto.LoginResponse;
import com.prompt2web.dto.RegisterRequest;
import com.prompt2web.dto.UserResponse;
import com.prompt2web.entity.User;
import com.prompt2web.repository.UserRepository;
import com.prompt2web.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        return new LoginResponse(
                "Login successful",
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}