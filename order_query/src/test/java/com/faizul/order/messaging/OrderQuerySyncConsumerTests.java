package com.faizul.order.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.faizul.order.event.OrderCreatedEvent;
import com.faizul.order.event.OrderDeletedEvent;
import com.faizul.order.event.OrderUpdatedEvent;
import com.faizul.order.query.OrderView;
import com.faizul.order.query.OrderViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderQuerySyncConsumerTests {

    @Mock
    private OrderViewRepository orderViewRepository;

    @InjectMocks
    private OrderQuerySyncConsumer consumer;

    @Test
    void handleOrderCreatedSavesNewOrderView() {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 10L, 20L, LocalDate.of(2026, 10, 23), "Alice", "alice@test.com", "Buku", 2, new BigDecimal("50000"));
        consumer.handleOrderCreated(event);

        ArgumentCaptor<OrderView> captor = ArgumentCaptor.forClass(OrderView.class);
        verify(orderViewRepository).save(captor.capture());
        OrderView view = captor.getValue();
        assertEquals(1L, view.getOrderId());
        assertEquals(10L, view.getProductId());
        assertEquals(20L, view.getPelangganId());
        assertEquals("Alice", view.getNamaPelanggan());
        assertEquals("Buku", view.getNamaProduk());
        assertEquals(2, view.getJumlah());
        assertEquals(new BigDecimal("50000"), view.getTotal());
    }

    @Test
    void handleOrderUpdatedUpdatesExistingOrderView() {
        OrderView existing = new OrderView(1L, 10L, "Buku", 20L, "Alice", "alice@test.com", LocalDate.of(2026, 10, 23), 2, new BigDecimal("50000"));
        when(orderViewRepository.findById(1L)).thenReturn(Optional.of(existing));

        OrderUpdatedEvent event = new OrderUpdatedEvent(1L, 10L, 20L, LocalDate.of(2026, 10, 23), "Alice", "alice@test.com", "Buku", 3, new BigDecimal("75000"));
        consumer.handleOrderUpdated(event);

        ArgumentCaptor<OrderView> captor = ArgumentCaptor.forClass(OrderView.class);
        verify(orderViewRepository).save(captor.capture());
        OrderView view = captor.getValue();
        assertEquals(10L, view.getProductId());
        assertEquals(20L, view.getPelangganId());
        assertEquals(3, view.getJumlah());
        assertEquals(new BigDecimal("75000"), view.getTotal());
    }

    @Test
    void handleOrderDeletedDeletesOrderView() {
        consumer.handleOrderDeleted(new OrderDeletedEvent(1L));
        verify(orderViewRepository).deleteById(1L);
    }
}
