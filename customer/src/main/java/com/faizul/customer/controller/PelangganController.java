package com.faizul.customer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.faizul.customer.entity.Pelanggan;
import com.faizul.customer.service.PelangganService;

@RestController
@RequestMapping("/api/pelanggan")
public class PelangganController {

    private final PelangganService pelangganService;

    public PelangganController(PelangganService pelangganService) {
        this.pelangganService = pelangganService;
    }

    @GetMapping
    public ResponseEntity<List<Pelanggan>> getAllPelanggan() {
        return ResponseEntity.ok(pelangganService.getAllPelanggan());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelanggan> getPelangganById(@PathVariable Long id) {
        Pelanggan pelanggan = pelangganService.getPelangganById(id);
        if (pelanggan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pelanggan);
    }

    @PostMapping
    public ResponseEntity<Pelanggan> createPelanggan(@RequestBody Pelanggan pelanggan) {
        return ResponseEntity.ok(pelangganService.createPelanggan(pelanggan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelanggan> updatePelanggan(@PathVariable Long id,
            @RequestBody Pelanggan pelanggan) {
        Pelanggan updated = pelangganService.updatePelanggan(id, pelanggan);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelanggan(@PathVariable Long id) {
        if (!pelangganService.deletePelanggan(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
