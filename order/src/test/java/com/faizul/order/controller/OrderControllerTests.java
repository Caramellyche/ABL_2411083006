package com.faizul.order.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.faizul.order.entity.Order;
import com.faizul.order.entity.OrderView;
import com.faizul.order.service.OrderCommandService;
import com.faizul.order.service.OrderQueryService;
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

    @Mock
    private OrderQueryService queryService;

    @InjectMocks
    private OrderController controller;

    @Test
    void getAllOrdersDelegatesToQueryService() {
        OrderView view = new OrderView(1L, 10L, "Kopi", 20L, "Budi", "budi@test.com", LocalDate.now(), 2, new BigDecimal("20000"));
        when(queryService.getAllOrders()).thenReturn(List.of(view));

        ResponseEntity<List<OrderView>> response = controller.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Kopi", response.getBody().get(0).getNamaProduk());
    }

    @Test
    void getOrderByIdDelegatesToQueryService() {
        OrderView view = new OrderView(1L, 10L, "Kopi", 20L, "Budi", "budi@test.com", LocalDate.now(), 2, new BigDecimal("20000"));
        when(queryService.getOrderById(1L)).thenReturn(view);

        ResponseEntity<OrderView> response = controller.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kopi", response.getBody().getNamaProduk());
    }

    @Test
    void getOrderByIdReturnsNotFoundWhenMissing() {
        when(queryService.getOrderById(99L)).thenReturn(null);

        ResponseEntity<OrderView> response = controller.getOrderById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

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
    void deleteOrderDelegatesToCommandService() {
        when(commandService.deleteOrder(1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteOrder(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
