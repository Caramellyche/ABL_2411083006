package com.faizul.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.faizul.product.entity.Pelanggan;

@Repository
public interface PelangganRepository extends JpaRepository<Pelanggan, Long> {

    List<Pelanggan> findByNamaContainingIgnoreCase(String nama);

    Optional<Pelanggan> findByEmail(String email);
}
