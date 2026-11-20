package com.faizul.auth.service;

import com.faizul.auth.dto.AuthResponse;
import com.faizul.auth.dto.LoginRequest;
import com.faizul.auth.dto.RegisterRequest;
import com.faizul.auth.entity.User;
import com.faizul.auth.repository.UserRepository;
import com.faizul.auth.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(RegisterRequest req) {
        if (userRepository.findByUsername(req.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        String role = (req.role() == null || req.role().isBlank()) ? "USER" : req.role().toUpperCase();
        User user = new User(null, req.username(), passwordEncoder.encode(req.password()), role);
        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}
