package com.faizul.customer.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.faizul.customer.vo.Produk;

/**
 * Klien untuk memanggil product service melalui Eureka.
 * Nama host "product" diselesaikan oleh Spring Cloud LoadBalancer
 * berdasarkan registrasi di Eureka (spring.application.name=product).
 */
@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient.Builder loadBalancedRestClientBuilder) {
        this.restClient = loadBalancedRestClientBuilder
                .baseUrl("http://product")
                .build();
    }

    /**
     * Ambil detail produk dari product service.
     * @return Produk jika ditemukan, null jika tidak ada (404).
     */
    public Produk getProduk(Long productId) {
        return restClient.get()
                .uri("/api/product/{id}", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                })
                .body(Produk.class);
    }
}
