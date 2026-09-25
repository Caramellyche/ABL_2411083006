package com.faizul.order.service;

import com.faizul.order.entity.Order;
import com.faizul.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id).orElse(null);
        if (existingOrder == null) {
            return null;
        }

        existingOrder.setProductId(order.getProductId());
        existingOrder.setPelangganId(order.getPelangganId());
        existingOrder.setTanggalTransaksi(order.getTanggalTransaksi());
        existingOrder.setJumlah(order.getJumlah());
        existingOrder.setTotal(order.getTotal());
        return orderRepository.save(existingOrder);
    }

    public boolean deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            return false;
        }

        orderRepository.deleteById(id);
        return true;
    }
}