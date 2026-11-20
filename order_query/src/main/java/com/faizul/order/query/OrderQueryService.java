package com.faizul.order.query;

import java.util.List;

import org.springframework.stereotype.Service;

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
