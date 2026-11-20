package com.faizul.gateway;

import com.faizul.gateway.filter.AuthenticationFilter;
import com.faizul.gateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationFilterTest {

    private JwtUtil jwtUtil;
    private AuthenticationFilter authenticationFilter;
    private GatewayFilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("test-only-signing-key-not-for-production-123456789");
        authenticationFilter = new AuthenticationFilter(jwtUtil);
        filterChain = mock(GatewayFilterChain.class);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void missingAuthorizationHeader_shouldReturn401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        filter.filter(exchange, filterChain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(filterChain, never()).filter(any());
    }

    @Test
    void invalidToken_shouldReturn401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        filter.filter(exchange, filterChain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(filterChain, never()).filter(any());
    }

    @Test
    void validToken_shouldPassThrough() {
        String token = jwtUtil.generateToken("user1", "USER");
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        filter.filter(exchange, filterChain).block();

        assertEquals(null, exchange.getResponse().getStatusCode());
        verify(filterChain, times(1)).filter(any());
    }
}
