package com.faizul.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.faizul.order.client.CustomerClient;
import com.faizul.order.client.ProductClient;
import com.faizul.order.entity.Order;
import com.faizul.order.event.OrderCreatedEvent;
import com.faizul.order.event.OrderDeletedEvent;
import com.faizul.order.event.OrderEventPublisher;
import com.faizul.order.event.OrderUpdatedEvent;
import com.faizul.order.repository.OrderRepository;
import com.faizul.order.vo.Pelanggan;
import com.faizul.order.vo.Produk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private OrderEventPublisher eventPublisher;

    @InjectMocks
    private OrderCommandService commandService;

    @Test
    void createOrderValidatesAndPublishesEvent() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            Order request = new Order(null, 1L, 2L, null, 2, null);
            when(productClient.getProduct(1L)).thenReturn(new Produk(1L, "Laptop", 5000000.0));
            Pelanggan customer = new Pelanggan("Andi", "Jakarta", "08123");
            customer.setEmail("andi@example.com");
            when(customerClient.getCustomer(2L)).thenReturn(customer);

            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
                Order o = inv.getArgument(0);
                o.setId(10L);
                return o;
            });

            Order created = commandService.createOrder(request);

            assertNotNull(created);
            assertEquals(10L, created.getId());
            assertEquals(0, new BigDecimal("10000000.0").compareTo(created.getTotal()));

            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(eventPublisher).publish(any(OrderCreatedEvent.class));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void updateOrderUpdatesDataAndPublishesUpdatedEvent() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            Order existing = new Order(10L, 1L, 2L, LocalDate.now(), 2, new BigDecimal("10000000.0"));
            when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));

            when(productClient.getProduct(1L)).thenReturn(new Produk(1L, "Laptop", 5000000.0));
            Pelanggan customer = new Pelanggan("Andi", "Jakarta", "08123");
            customer.setEmail("andi@example.com");
            when(customerClient.getCustomer(2L)).thenReturn(customer);

            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order updateRequest = new Order(null, 1L, 2L, LocalDate.now(), 3, null);
            Order updated = commandService.updateOrder(10L, updateRequest);

            assertNotNull(updated);
            assertEquals(3, updated.getJumlah());
            assertEquals(0, new BigDecimal("15000000.0").compareTo(updated.getTotal()));

            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(eventPublisher).publish(any(OrderUpdatedEvent.class));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void deleteOrderRemovesEntityAndPublishesDeletedEvent() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            when(orderRepository.existsById(10L)).thenReturn(true);

            boolean deleted = commandService.deleteOrder(10L);

            assertTrue(deleted);
            verify(orderRepository).deleteById(10L);

            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(eventPublisher).publish(new OrderDeletedEvent(10L));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
