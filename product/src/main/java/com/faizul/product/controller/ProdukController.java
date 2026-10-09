package com.faizul.product.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.faizul.product.entity.Product;
import com.faizul.product.service.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProdukController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProduk(
            @RequestParam(name = "idjenis", required = false) Long idJenis) {
        if (idJenis != null) {
            return ResponseEntity.ok(productService.getProductsByJenisId(idJenis));
        }
        return ResponseEntity.ok(productService.getAllProduk());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProdukById(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/jenis/{jenisId}")
    public ResponseEntity<List<Product>> getProdukByJenisId(@PathVariable("jenisId") Long jenisId) {
        return ResponseEntity.ok(productService.getProductsByJenisId(jenisId));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduk(@PathVariable("id") Long id, @RequestBody Product product) {
        Product existingProduct = productService.updateProduct(id, product);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(existingProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduk(@PathVariable("id") Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
