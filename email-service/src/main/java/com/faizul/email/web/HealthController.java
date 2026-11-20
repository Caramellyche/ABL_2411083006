package com.faizul.email.web;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/api/emails/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "mode", "RabbitMQ consumer");
    }
}
