package com.faizul.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Pelanggan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nama;
    private String email;
    private String nomorTelepon;
    private String alamat;

    @ManyToMany
    @JoinTable(
        name = "pelanggan_product",
        joinColumns = @JoinColumn(name = "pelanggan_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @JsonIgnoreProperties("pelangganList")
    private List<Product> products = new ArrayList<>();

    public Pelanggan() {
    }

    public Pelanggan(Long id, String nama, String email, String nomorTelepon, String alamat) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.nomorTelepon = nomorTelepon;
        this.alamat = alamat;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
