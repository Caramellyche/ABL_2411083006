package com.faizul.email.service;

import com.faizul.order.event.OrderCreatedEvent;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OrderConfirmationEmailService {
    private final JavaMailSender mailSender;
    private final String from;

    public OrderConfirmationEmailService(JavaMailSender mailSender,
            @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void send(OrderCreatedEvent event) {
        if (event == null || event.orderId() == null || event.tanggalTransaksi() == null
                || isBlank(event.customerName()) || isBlank(event.customerEmail())
                || isBlank(event.productName()) || event.quantity() == null || event.quantity() <= 0
                || event.total() == null || event.total().signum() < 0) {
            throw new IllegalArgumentException("Order confirmation event has missing or invalid fields");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.customerEmail());
        message.setSubject("Konfirmasi Pesanan #" + event.orderId());
        message.setText(String.format("Halo %s,%n%nTerima kasih, pesanan Anda telah berhasil dibuat!%n"
                        + "=====================================================%n  DETAIL PESANAN%n"
                        + "=====================================================%n  ID Pesanan    : #%d%n  Tanggal       : %s%n"
                        + "  Produk        : %s%n  Jumlah        : %d%n  Total Tagihan : Rp %,.2f%n"
                        + "=====================================================%n%nPesanan Anda saat ini sedang kami proses.%n%n"
                        + "Salam hangat,%nTim Layanan Pelanggan", event.customerName(), event.orderId(),
                event.tanggalTransaksi(), event.productName(), event.quantity(), event.total()));
        mailSender.send(message);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
