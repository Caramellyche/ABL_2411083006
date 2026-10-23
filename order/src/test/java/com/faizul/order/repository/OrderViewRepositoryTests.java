package com.faizul.order.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.faizul.order.entity.OrderView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderViewRepositoryTests {

    @Autowired
    private OrderViewRepository orderViewRepository;

    @Test
    void saveFindByIdAndFindAll() {
        OrderView view1 = new OrderView(
                100L,
                10L,
                "Keyboard",
                20L,
                "Alice",
                "alice@example.com",
                LocalDate.of(2026, 10, 23),
                2,
                new BigDecimal("50000.00")
        );

        orderViewRepository.save(view1);

        Optional<OrderView> found = orderViewRepository.findById(100L);
        assertTrue(found.isPresent());
        assertEquals("Keyboard", found.get().getNamaProduk());
        assertEquals("Alice", found.get().getNamaPelanggan());
        assertEquals("alice@example.com", found.get().getEmailPelanggan());
        assertEquals(10L, found.get().getProductId());
        assertEquals(20L, found.get().getPelangganId());
        assertEquals(LocalDate.of(2026, 10, 23), found.get().getTanggalTransaksi());
        assertEquals(2, found.get().getJumlah());
        assertEquals(new BigDecimal("50000.00"), found.get().getTotal());
        assertEquals(100L, found.get().getId());
        assertEquals(100L, found.get().getOrderId());

        List<OrderView> all = orderViewRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(100L, all.get(0).getOrderId());
    }
}
