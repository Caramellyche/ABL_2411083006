package com.faizul.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.faizul.product.entity.Pelanggan;
import com.faizul.product.entity.Product;
import com.faizul.product.repository.PelangganRepository;
import com.faizul.product.repository.ProductRepository;

@Service
public class PelangganService {

    @Autowired
    private PelangganRepository pelangganRepository;

    @Autowired
    private ProductRepository productRepository;

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
        Pelanggan existingPelanggan = pelangganRepository.findById(id).orElse(null);
        if (existingPelanggan == null) {
            return null;
        }
        existingPelanggan.setNama(pelanggan.getNama());
        existingPelanggan.setEmail(pelanggan.getEmail());
        existingPelanggan.setNomorTelepon(pelanggan.getNomorTelepon());
        existingPelanggan.setAlamat(pelanggan.getAlamat());
        return pelangganRepository.save(existingPelanggan);
    }

    public boolean deletePelanggan(Long id) {
        if (!pelangganRepository.existsById(id)) {
            return false;
        }
        pelangganRepository.deleteById(id);
        return true;
    }

    public Pelanggan addProductToPelanggan(Long pelangganId, Long productId) {
        Pelanggan pelanggan = pelangganRepository.findById(pelangganId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (pelanggan != null && product != null) {
            if (!pelanggan.getProducts().contains(product)) {
                pelanggan.getProducts().add(product);
                return pelangganRepository.save(pelanggan);
            }
            return pelanggan;
        }
        return null;
    }
}
