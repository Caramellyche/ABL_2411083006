package com.faizul.product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faizul.product.entity.JenisProduct;
import com.faizul.product.repository.JenisProductRepository;

@Service
public class JenisProductService {

    @Autowired
    private JenisProductRepository jenisProductRepository;

    public List<JenisProduct> getAllJenisProduct() {
        return jenisProductRepository.findAll();
    }

    public JenisProduct getJenisProductById(Long id) {
        return jenisProductRepository.findById(id).orElse(null);
    }

    public JenisProduct createJenisProduct(JenisProduct jenisProduct) {
        return jenisProductRepository.save(jenisProduct);
    }

    public JenisProduct updateJenisProduct(Long id, JenisProduct jenisProduct) {
        JenisProduct existing = jenisProductRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setNamaJenis(jenisProduct.getNamaJenis());
        existing.setDeskripsi(jenisProduct.getDeskripsi());
        return jenisProductRepository.save(existing);
    }

    public boolean deleteJenisProduct(Long id) {
        if (!jenisProductRepository.existsById(id)) {
            return false;
        }
        jenisProductRepository.deleteById(id);
        return true;
    }
}
