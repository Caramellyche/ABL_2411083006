package com.faizul.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.faizul.product.entity.JenisProduct;
import com.faizul.product.service.JenisProductService;

@RestController
@RequestMapping("/api/product/jenis-product")
public class JenisProductController {

    @Autowired
    private JenisProductService jenisProductService;

    @GetMapping
    public ResponseEntity<List<JenisProduct>> getAllJenisProduct() {
        return ResponseEntity.ok(jenisProductService.getAllJenisProduct());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JenisProduct> getJenisProductById(@PathVariable("id") Long id) {
        JenisProduct jenisProduct = jenisProductService.getJenisProductById(id);
        if (jenisProduct == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(jenisProduct);
    }

    @PostMapping
    public ResponseEntity<JenisProduct> createJenisProduct(@RequestBody JenisProduct jenisProduct) {
        JenisProduct created = jenisProductService.createJenisProduct(jenisProduct);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JenisProduct> updateJenisProduct(@PathVariable("id") Long id,
            @RequestBody JenisProduct jenisProduct) {
        JenisProduct updated = jenisProductService.updateJenisProduct(id, jenisProduct);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJenisProduct(@PathVariable("id") Long id) {
        boolean deleted = jenisProductService.deleteJenisProduct(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
