package com.faizul.order.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderCreatedEvent(Long orderId, Long productId, Long pelangganId,
                                LocalDate tanggalTransaksi, String customerName,
                                String customerEmail, String productName, Integer quantity,
                                BigDecimal total) {
}
