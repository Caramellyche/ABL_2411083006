package com.faizul.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.faizul.order.config.RabbitConfig;
import com.faizul.order.entity.OrderView;
import com.faizul.order.event.OrderCreatedEvent;
import com.faizul.order.event.OrderDeletedEvent;
import com.faizul.order.event.OrderUpdatedEvent;
import com.faizul.order.repository.OrderViewRepository;

@Component
@RabbitListener(queues = RabbitConfig.QUERY_SYNC_QUEUE)
public class OrderQuerySyncConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderQuerySyncConsumer.class);

    private final OrderViewRepository orderViewRepository;

    public OrderQuerySyncConsumer(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    @RabbitHandler
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Syncing OrderCreatedEvent for orderId: {}", event.orderId());
        OrderView view = new OrderView(
                event.orderId(),
                null,
                event.productName(),
                null,
                event.customerName(),
                event.customerEmail(),
                event.tanggalTransaksi(),
                event.quantity(),
                event.total()
        );
        orderViewRepository.save(view);
    }

    @RabbitHandler
    @Transactional
    public void handleOrderUpdated(OrderUpdatedEvent event) {
        log.info("Syncing OrderUpdatedEvent for orderId: {}", event.orderId());
        OrderView view = orderViewRepository.findById(event.orderId()).orElse(new OrderView());
        view.setOrderId(event.orderId());
        view.setNamaProduk(event.productName());
        view.setNamaPelanggan(event.customerName());
        view.setEmailPelanggan(event.customerEmail());
        view.setTanggalTransaksi(event.tanggalTransaksi());
        view.setJumlah(event.quantity());
        view.setTotal(event.total());
        orderViewRepository.save(view);
    }

    @RabbitHandler
    @Transactional
    public void handleOrderDeleted(OrderDeletedEvent event) {
        log.info("Syncing OrderDeletedEvent for orderId: {}", event.orderId());
        orderViewRepository.deleteById(event.orderId());
    }
}
