package com.faizul.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.faizul.product.entity.JenisProduct;

@Repository
public interface JenisProductRepository extends JpaRepository<JenisProduct, Long> {

}
