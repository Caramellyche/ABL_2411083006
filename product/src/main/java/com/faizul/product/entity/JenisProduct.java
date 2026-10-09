package com.faizul.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class JenisProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String namaJenis;
    private String deskripsi;

    public JenisProduct() {
    }

    public JenisProduct(Long id, String namaJenis, String deskripsi) {
        this.id = id;
        this.namaJenis = namaJenis;
        this.deskripsi = deskripsi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaJenis() {
        return namaJenis;
    }

    public void setNamaJenis(String namaJenis) {
        this.namaJenis = namaJenis;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
