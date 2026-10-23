package com.faizul.order.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderCreatedEvent(Long orderId, LocalDate tanggalTransaksi, String customerName,
                                String customerEmail, String productName, Integer quantity,
                                BigDecimal total) {
}
