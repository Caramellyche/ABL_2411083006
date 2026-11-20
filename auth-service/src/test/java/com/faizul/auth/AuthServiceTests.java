package com.faizul.auth;

import com.faizul.auth.dto.AuthResponse;
import com.faizul.auth.dto.LoginRequest;
import com.faizul.auth.dto.RegisterRequest;
import com.faizul.auth.entity.User;
import com.faizul.auth.repository.UserRepository;
import com.faizul.auth.service.AuthService;
import com.faizul.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtUtil = new JwtUtil("test-only-signing-key-not-for-production-123456789");
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void register_shouldHashPasswordAndSaveUser() {
        RegisterRequest req = new RegisterRequest("faizul", "password123", "ADMIN");
        when(userRepository.findByUsername("faizul")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User saved = authService.register(req);
        assertNotNull(saved);
        assertEquals("faizul", saved.getUsername());
        assertTrue(passwordEncoder.matches("password123", saved.getPassword()));
        assertEquals("ADMIN", saved.getRole());
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        RegisterRequest req = new RegisterRequest("faizul", "password123", "USER");
        when(userRepository.findByUsername("faizul")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    void login_shouldReturnJwtTokenWhenCredentialsValid() {
        String hashed = passwordEncoder.encode("secret");
        User user = new User(1L, "faizul", hashed, "USER");
        when(userRepository.findByUsername("faizul")).thenReturn(Optional.of(user));

        LoginRequest loginReq = new LoginRequest("faizul", "secret");
        AuthResponse response = authService.login(loginReq);

        assertNotNull(response.token());
        assertTrue(jwtUtil.validateToken(response.token()));
        assertEquals("faizul", response.username());
    }

    @Test
    void login_shouldThrowWhenPasswordWrong() {
        String hashed = passwordEncoder.encode("secret");
        User user = new User(1L, "faizul", hashed, "USER");
        when(userRepository.findByUsername("faizul")).thenReturn(Optional.of(user));

        LoginRequest loginReq = new LoginRequest("faizul", "wrongpass");
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginReq));
    }
}
