package com.faizul.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

@Service
public class OrderCommandService {

    private static final Logger log = LoggerFactory.getLogger(OrderCommandService.class);

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final CustomerClient customerClient;
    private final OrderEventPublisher eventPublisher;

    public OrderCommandService(OrderRepository orderRepository, ProductClient productClient,
                               CustomerClient customerClient, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.customerClient = customerClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order createOrder(Order order) {
        validateOrderRequest(order);

        Produk product = productClient.getProduct(order.getProductId());
        Pelanggan customer = customerClient.getCustomer(order.getPelangganId());
        validateFetchedData(product, customer);

        order.setTanggalTransaksi(order.getTanggalTransaksi() == null ? LocalDate.now() : order.getTanggalTransaksi());
        order.setTotal(BigDecimal.valueOf(product.getHarga()).multiply(BigDecimal.valueOf(order.getJumlah())));
        Order saved = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(saved.getId(), saved.getTanggalTransaksi(),
                customer.getNama(), customer.getEmail(), product.getNama(), saved.getJumlah(), saved.getTotal());

        // ponytail: no outbox; add outbox when delivery guarantee across process crashes is required.
        registerPostCommitSync(() -> eventPublisher.publish(event), saved.getId(), "created");

        return saved;
    }

    @Transactional
    public Order updateOrder(Long id, Order order) {
        Order existing = orderRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        Long productId = order.getProductId() != null ? order.getProductId() : existing.getProductId();
        Long pelangganId = order.getPelangganId() != null ? order.getPelangganId() : existing.getPelangganId();
        Integer jumlah = order.getJumlah() != null ? order.getJumlah() : existing.getJumlah();

        if (jumlah == null || jumlah <= 0) {
            throw new IllegalArgumentException("positive jumlah is required");
        }

        Produk product = productClient.getProduct(productId);
        Pelanggan customer = customerClient.getCustomer(pelangganId);
        validateFetchedData(product, customer);

        existing.setProductId(productId);
        existing.setPelangganId(pelangganId);
        existing.setJumlah(jumlah);
        existing.setTanggalTransaksi(order.getTanggalTransaksi() != null ? order.getTanggalTransaksi() : existing.getTanggalTransaksi());
        existing.setTotal(BigDecimal.valueOf(product.getHarga()).multiply(BigDecimal.valueOf(jumlah)));

        Order saved = orderRepository.save(existing);

        OrderUpdatedEvent event = new OrderUpdatedEvent(saved.getId(), saved.getTanggalTransaksi(),
                customer.getNama(), customer.getEmail(), product.getNama(), saved.getJumlah(), saved.getTotal());

        registerPostCommitSync(() -> eventPublisher.publish(event), saved.getId(), "updated");

        return saved;
    }

    @Transactional
    public boolean deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            return false;
        }

        orderRepository.deleteById(id);

        OrderDeletedEvent event = new OrderDeletedEvent(id);
        registerPostCommitSync(() -> eventPublisher.publish(event), id, "deleted");

        return true;
    }

    private void validateOrderRequest(Order order) {
        if (order.getProductId() == null || order.getPelangganId() == null
                || order.getJumlah() == null || order.getJumlah() <= 0) {
            throw new IllegalArgumentException("productId, pelangganId, and positive jumlah are required");
        }
    }

    private void validateFetchedData(Produk product, Pelanggan customer) {
        if (product == null || product.getNama() == null || product.getHarga() <= 0
                || customer == null || customer.getNama() == null
                || customer.getEmail() == null || customer.getEmail().isBlank()) {
            throw new IllegalArgumentException("Product or customer data is missing or invalid");
        }
    }

    private void registerPostCommitSync(Runnable publishAction, Long orderId, String eventType) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        publishAction.run();
                    } catch (RuntimeException exception) {
                        log.error("Order {} {} event publish failed", orderId, eventType, exception);
                    }
                }
            });
        } else {
            try {
                publishAction.run();
            } catch (RuntimeException exception) {
                log.error("Order {} {} event publish failed (non-tx)", orderId, eventType, exception);
            }
        }
    }
}
