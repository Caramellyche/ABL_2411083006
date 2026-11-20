package com.faizul.gateway;

import com.faizul.gateway.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtSecretConfigurationTests {

    private static final String TEST_SECRET = "test-only-signing-key-not-for-production-123456789";

    @Test
    void configuredSecret_shouldValidateTokensSignedWithSharedKey() {
        try (var context = contextWithSecret(TEST_SECRET)) {
            context.refresh();
            JwtUtil jwt = context.getBean(JwtUtil.class);
            String token = Jwts.builder().subject("user1").claim("role", "USER")
                    .signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            assertTrue(jwt.validateToken(token));
            assertEquals("user1", jwt.extractClaims(token).getSubject());
            String otherToken = Jwts.builder().subject("user1")
                    .signWith(Keys.hmacShaKeyFor(
                            "another-test-only-key-12345678901234567890".getBytes(StandardCharsets.UTF_8)))
                    .compact();
            assertFalse(jwt.validateToken(otherToken));
        }
    }

    @Test
    void emptyOrWeakSecret_shouldPreventStartup() {
        for (String secret : new String[]{"", "short"}) {
            try (var context = contextWithSecret(secret)) {
                assertThrows(RuntimeException.class, context::refresh);
            }
        }
    }

    private AnnotationConfigApplicationContext contextWithSecret(String secret) {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources()
                .addFirst(new MapPropertySource("test", Map.of("JWT_SECRET", secret)));
        context.register(JwtUtil.class);
        return context;
    }
}
