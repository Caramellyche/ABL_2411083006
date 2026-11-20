package com.faizul.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "eureka.client.enabled=false")
class RouteConfigTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void testRoutesConfigured() {
        List<String> routeIds = routeLocator.getRoutes()
                .map(org.springframework.cloud.gateway.route.Route::getId)
                .collectList()
                .block();

        assertEquals(5, routeIds.size());
        assertTrue(routeIds.contains("auth-route"));
        assertTrue(routeIds.contains("order-query-route"));
        assertTrue(routeIds.contains("order-command-route"));
        assertTrue(routeIds.contains("product-route"));
        assertTrue(routeIds.contains("customer-route"));
    }
}
