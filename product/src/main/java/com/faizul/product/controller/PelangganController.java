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

import com.faizul.product.entity.Pelanggan;
import com.faizul.product.service.PelangganService;

import java.util.List;

@RestController
@RequestMapping("/api/pelanggan")
public class PelangganController {

    @Autowired
    private PelangganService pelangganService;

    @GetMapping
    public ResponseEntity<List<Pelanggan>> getAllPelanggan() {
        return ResponseEntity.ok(pelangganService.getAllPelanggan());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelanggan> getPelangganById(@PathVariable("id") Long id) {
        Pelanggan pelanggan = pelangganService.getPelangganById(id);
        if (pelanggan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pelanggan);
    }

    @PostMapping
    public ResponseEntity<Pelanggan> createPelanggan(@RequestBody Pelanggan pelanggan) {
        Pelanggan created = pelangganService.createPelanggan(pelanggan);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelanggan> updatePelanggan(@PathVariable("id") Long id, @RequestBody Pelanggan pelanggan) {
        Pelanggan updated = pelangganService.updatePelanggan(id, pelanggan);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelanggan(@PathVariable("id") Long id) {
        boolean deleted = pelangganService.deletePelanggan(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/product/{productId}")
    public ResponseEntity<Pelanggan> addProductToPelanggan(
            @PathVariable("id") Long id,
            @PathVariable("productId") Long productId) {
        Pelanggan result = pelangganService.addProductToPelanggan(id, productId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
