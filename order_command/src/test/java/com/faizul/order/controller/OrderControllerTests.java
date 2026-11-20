package com.faizul.order.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.faizul.order.command.Order;
import com.faizul.order.command.OrderCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderControllerTests {

    @Mock
    private OrderCommandService commandService;

    @InjectMocks
    private OrderController controller;

    @Test
    void createOrderDelegatesToCommandService() {
        Order request = new Order(null, 10L, 20L, null, 2, null);
        Order created = new Order(1L, 10L, 20L, LocalDate.now(), 2, new BigDecimal("20000"));
        when(commandService.createOrder(request)).thenReturn(created);

        ResponseEntity<Order> response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void updateOrderDelegatesToCommandService() {
        Order request = new Order(null, 10L, 20L, null, 3, null);
        Order updated = new Order(1L, 10L, 20L, LocalDate.now(), 3, new BigDecimal("30000"));
        when(commandService.updateOrder(1L, request)).thenReturn(updated);

        ResponseEntity<Order> response = controller.updateOrder(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getJumlah());
    }

    @Test
    void updateOrderReturnsNotFoundWhenMissing() {
        Order request = new Order(null, 10L, 20L, null, 3, null);
        when(commandService.updateOrder(99L, request)).thenReturn(null);

        ResponseEntity<Order> response = controller.updateOrder(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteOrderDelegatesToCommandService() {
        when(commandService.deleteOrder(1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteOrder(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteOrderReturnsNotFoundWhenMissing() {
        when(commandService.deleteOrder(99L)).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteOrder(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
