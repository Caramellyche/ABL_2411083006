package com.faizul.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.faizul.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Query pencarian berdasarkan ID jenis product
    List<Product> findByJenisProductId(Long jenisProductId);

    // Query pencarian berdasarkan nama jenis product
    List<Product> findByJenisProductNamaJenisIgnoreCase(String namaJenis);

    // Query pencarian berdasarkan nama produk yang mengandung kata kunci (search)
    List<Product> findByNamaContainingIgnoreCase(String nama);

    // Contoh Custom JPQL Query dengan Join ke JenisProduct
    @Query("SELECT p FROM Product p JOIN p.jenisProduct j WHERE LOWER(j.namaJenis) = LOWER(:namaJenis)")
    List<Product> cariProdukBerdasarkanJenis(@Param("namaJenis") String namaJenis);
}
