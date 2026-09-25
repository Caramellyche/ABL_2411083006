package com.faizul.order.vo;


public class ResponseTemplate {
    private Pelanggan pelanggan;
    private Produk produk;

    public ResponseTemplate(Pelanggan pelanggan, Produk produk) {
        this.pelanggan = pelanggan;
        this.produk = produk;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
    }

    public Produk getProduk() {
        return produk;
    }

    public void setProduk(Produk produk) {
        this.produk = produk;
    }
}