package com.faizul.email.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

import com.faizul.order.event.OrderCreatedEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class OrderConfirmationEmailServiceTests {
    @Mock JavaMailSender mailSender;
    @InjectMocks OrderConfirmationEmailService service;

    @Test
    void sendsOrderEmailUsingCustomerAndProductValues() {
        service.send(new OrderCreatedEvent(2L, 10L, 20L, LocalDate.of(2026, 10, 16), "Panjul",
                "panjul@example.com", "Kopi", 2, new BigDecimal("24000000.00")));

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void rejectsMissingEmailAndProductInsteadOfSendingPlaceholders() {
        assertThrows(IllegalArgumentException.class, () -> service.send(new OrderCreatedEvent(2L,
                10L, 20L, LocalDate.of(2026, 10, 16), "Panjul", " ", null, 2, new BigDecimal("1"))));
    }
}
