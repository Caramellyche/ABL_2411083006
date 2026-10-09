package com.faizul.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.faizul.customer.entity.Pelanggan;
import com.faizul.customer.repository.PelangganRepository;

@Service
public class PelangganService {

    private final PelangganRepository pelangganRepository;

    public PelangganService(PelangganRepository pelangganRepository) {
        this.pelangganRepository = pelangganRepository;
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
}
