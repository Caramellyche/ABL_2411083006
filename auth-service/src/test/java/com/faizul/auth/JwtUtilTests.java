package com.faizul.auth;

import com.faizul.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTests {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("test-only-signing-key-not-for-production-123456789");
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtUtil.generateToken("user1", "USER");
        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("user1", jwtUtil.extractUsername(token));
        assertEquals("USER", jwtUtil.extractRole(token));
    }

    @Test
    void validateToken_shouldReturnFalseForTamperedToken() {
        String token = jwtUtil.generateToken("user1", "USER");
        String tampered = token + "xyz";
        assertFalse(jwtUtil.validateToken(tampered));
    }
}
