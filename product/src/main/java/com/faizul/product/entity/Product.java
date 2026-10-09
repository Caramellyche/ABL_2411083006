package com.faizul.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nama;
    private double harga;
    private String deskripsi;

    @ManyToOne
    @JoinColumn(name = "jenis_product_id")
    private JenisProduct jenisProduct;

    public Product() {
    }

    public Product(Long id, String nama, double harga, String deskripsi) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.deskripsi = deskripsi;
    }

    public Product(Long id, String nama, double harga, String deskripsi, JenisProduct jenisProduct) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.jenisProduct = jenisProduct;
    }

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

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public JenisProduct getJenisProduct() {
        return jenisProduct;
    }

    public void setJenisProduct(JenisProduct jenisProduct) {
        this.jenisProduct = jenisProduct;
    }
}
