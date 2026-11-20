package com.faizul.customer.client;

import java.net.URI;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.faizul.customer.vo.Produk;

/**
 * Klien untuk memanggil product service melalui Eureka.
 *
 * PENTING: kita TIDAK memakai bean global `@LoadBalanced RestClient.Builder`,
 * karena bean global tersebut ikut dipakai Eureka client sebagai transport HTTP
 * dan membuat registrasi Eureka gagal ("No instances available for localhost").
 *
 * Sebagai gantinya, service instance di-resolve eksplisit lewat LoadBalancerClient
 * lalu dipanggil dengan RestClient biasa.
 */
@Component
public class ProductClient {

    private final LoadBalancerClient loadBalancerClient;
    private final RestClient restClient = RestClient.create();

    public ProductClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    /**
     * Ambil detail produk dari product service.
     * @return Produk jika ditemukan, null jika tidak ada (404).
     */
    public Produk getProduk(Long productId) {
        ServiceInstance instance = loadBalancerClient.choose("product");
        if (instance == null) {
            return null;
        }

        URI uri = instance.getUri().resolve("/api/product/" + productId);
        return restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                })
                .body(Produk.class);
    }
}
