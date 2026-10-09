package com.faizul.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.faizul.customer.client.ProductClient;
import com.faizul.customer.entity.Pelanggan;
import com.faizul.customer.repository.PelangganRepository;
import com.faizul.customer.vo.Produk;

@Service
public class PelangganService {

    private final PelangganRepository pelangganRepository;
    private final ProductClient productClient;

    public PelangganService(PelangganRepository pelangganRepository, ProductClient productClient) {
        this.pelangganRepository = pelangganRepository;
        this.productClient = productClient;
    }

    public List<Pelanggan> getAllPelanggan() {
        return pelangganRepository.findAll();
    }

    public Pelanggan getPelangganById(Long id) {
        return pelangganRepository.findById(id).orElse(null);
    }

    public Pelanggan createPelanggan(Pelanggan pelanggan) {
        return pelangganRepository.save(pelanggan);
    }

    public Pelanggan updatePelanggan(Long id, Pelanggan pelanggan) {
        Pelanggan existing = pelangganRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setNama(pelanggan.getNama());
        existing.setEmail(pelanggan.getEmail());
        existing.setNomorTelepon(pelanggan.getNomorTelepon());
        existing.setAlamat(pelanggan.getAlamat());
        return pelangganRepository.save(existing);
    }

    public boolean deletePelanggan(Long id) {
        if (!pelangganRepository.existsById(id)) {
            return false;
        }
        pelangganRepository.deleteById(id);
        return true;
    }

    /**
     * Hubungkan pelanggan dengan produk dari product service.
     * Verifikasi produk lewat panggilan antar-service (Eureka).
     * @return Pelanggan yang sudah diperbarui, atau null jika pelanggan/produk tidak ada.
     */
    public Pelanggan addProduct(Long pelangganId, Long productId) {
        Pelanggan pelanggan = pelangganRepository.findById(pelangganId).orElse(null);
        if (pelanggan == null) {
            return null;
        }

        Produk produk = productClient.getProduk(productId);
        if (produk == null) {
            return null;
        }

        if (!pelanggan.getProductIds().contains(productId)) {
            pelanggan.getProductIds().add(productId);
        }
        return pelangganRepository.save(pelanggan);
    }

    /**
     * Ambil pelanggan beserta detail produk (dari product service).
     */
    public PelangganDetail getDetail(Long pelangganId) {
        Pelanggan pelanggan = pelangganRepository.findById(pelangganId).orElse(null);
        if (pelanggan == null) {
            return null;
        }

        java.util.List<Produk> produkList = new java.util.ArrayList<>();
        for (Long productId : pelanggan.getProductIds()) {
            Produk produk = productClient.getProduk(productId);
            if (produk != null) {
                produkList.add(produk);
            }
        }
        return new PelangganDetail(pelanggan, produkList);
    }

    public record PelangganDetail(Pelanggan pelanggan, java.util.List<Produk> produk) {
    }
}
