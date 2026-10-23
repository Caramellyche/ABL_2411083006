package com.faizul.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.faizul.order.entity.OrderView;
import com.faizul.order.repository.OrderViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTests {

    @Mock
    private OrderViewRepository orderViewRepository;

    @InjectMocks
    private OrderQueryService queryService;

    @Test
    void getAllOrdersReturnsListFromViewRepository() {
        OrderView view = new OrderView(1L, 10L, "Kopi", 20L, "Budi", "budi@test.com", LocalDate.now(), 1, new BigDecimal("15000"));
        when(orderViewRepository.findAll()).thenReturn(List.of(view));

        List<OrderView> orders = queryService.getAllOrders();

        assertEquals(1, orders.size());
        assertEquals("Kopi", orders.get(0).getNamaProduk());
    }

    @Test
    void getOrderByIdReturnsOrderViewWhenFound() {
        OrderView view = new OrderView(1L, 10L, "Kopi", 20L, "Budi", "budi@test.com", LocalDate.now(), 1, new BigDecimal("15000"));
        when(orderViewRepository.findById(1L)).thenReturn(Optional.of(view));

        OrderView result = queryService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("Budi", result.getNamaPelanggan());
    }

    @Test
    void getOrderByIdReturnsNullWhenNotFound() {
        when(orderViewRepository.findById(99L)).thenReturn(Optional.empty());

        OrderView result = queryService.getOrderById(99L);

        assertNull(result);
    }
}
