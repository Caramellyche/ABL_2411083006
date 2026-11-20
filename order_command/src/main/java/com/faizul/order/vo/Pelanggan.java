package com.faizul.order.vo;


public class Pelanggan {
    private Long id;
    private String nama;
    private String email;
    private String alamat;
    private String noTelp;

    public Pelanggan() {
    }

    public Pelanggan(Long id, String nama, String email, String alamat, String noTelp) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.alamat = alamat;
        this.noTelp = noTelp;
    }

    public Pelanggan(String nama, String alamat, String noTelp) {
        this.nama = nama;
        this.alamat = alamat;
        this.noTelp = noTelp;
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

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }
}
