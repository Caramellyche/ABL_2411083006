package com.faizul.order.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.faizul.order.entity.OrderView;
import com.faizul.order.repository.OrderViewRepository;

@Service
public class OrderQueryService {

    private final OrderViewRepository orderViewRepository;

    public OrderQueryService(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    public List<OrderView> getAllOrders() {
        return orderViewRepository.findAll();
    }

    public OrderView getOrderById(Long id) {
        return orderViewRepository.findById(id).orElse(null);
    }
}
