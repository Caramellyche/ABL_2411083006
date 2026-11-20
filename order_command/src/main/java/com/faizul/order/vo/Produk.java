package com.faizul.order.vo;

public class Produk {
    private Long id;
    private String nama;
    private Double harga;

    // Constructors
    public Produk() {}

    public Produk(Long id, String nama, Double harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Double getHarga() {
        return harga;
    }

    public void setHarga(Double harga) {
        this.harga = harga;
    }
}