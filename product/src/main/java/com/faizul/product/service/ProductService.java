package com.faizul.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.faizul.product.entity.Product;
import com.faizul.product.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getAllProduk() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            return null;
        }
        existingProduct.setNama(product.getNama());
        existingProduct.setHarga(product.getHarga());
        existingProduct.setDeskripsi(product.getDeskripsi());
        existingProduct.setJenisProduct(product.getJenisProduct());
        return productRepository.save(existingProduct);
    }

    public List<Product> getProductsByJenisId(Long jenisProductId) {
        return productRepository.findByJenisProductId(jenisProductId);
    }

    public List<Product> getProductsByNamaJenis(String namaJenis) {
        return productRepository.findByJenisProductNamaJenisIgnoreCase(namaJenis);
    }

    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

}
