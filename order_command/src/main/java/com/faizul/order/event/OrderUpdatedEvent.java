package com.faizul.order.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderUpdatedEvent(Long orderId, Long productId, Long pelangganId,
                                LocalDate tanggalTransaksi, String customerName,
                                String customerEmail, String productName, Integer quantity,
                                BigDecimal total) {
}
