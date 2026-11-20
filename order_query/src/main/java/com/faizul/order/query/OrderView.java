package com.faizul.order.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "order_views")
public class OrderView {

    @Id
    private Long orderId;

    private Long productId;
    private String namaProduk;
    private Long pelangganId;
    private String namaPelanggan;
    private String emailPelanggan;
    private LocalDate tanggalTransaksi;
    private Integer jumlah;
    private BigDecimal total;

    public OrderView() {
    }

    public OrderView(Long orderId, Long productId, String namaProduk, Long pelangganId,
                     String namaPelanggan, String emailPelanggan, LocalDate tanggalTransaksi,
                     Integer jumlah, BigDecimal total) {
        this.orderId = orderId;
        this.productId = productId;
        this.namaProduk = namaProduk;
        this.pelangganId = pelangganId;
        this.namaPelanggan = namaPelanggan;
        this.emailPelanggan = emailPelanggan;
        this.tanggalTransaksi = tanggalTransaksi;
        this.jumlah = jumlah;
        this.total = total;
    }

    public Long getId() {
        return orderId;
    }

    public void setId(Long id) {
        this.orderId = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public Long getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(Long pelangganId) {
        this.pelangganId = pelangganId;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getEmailPelanggan() {
        return emailPelanggan;
    }

    public void setEmailPelanggan(String emailPelanggan) {
        this.emailPelanggan = emailPelanggan;
    }

    public LocalDate getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(LocalDate tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public Integer getJumlah() {
        return jumlah;
    }

    public void setJumlah(Integer jumlah) {
        this.jumlah = jumlah;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
