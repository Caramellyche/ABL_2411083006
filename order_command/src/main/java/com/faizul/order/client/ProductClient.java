package com.faizul.order.client;

import java.net.URI;

import com.faizul.order.vo.Produk;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ProductClient {
    private final LoadBalancerClient loadBalancerClient;
    private final RestClient restClient = RestClient.create();

    public ProductClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    public Produk getProduct(Long productId) {
        ServiceInstance instance = loadBalancerClient.choose("product");
        if (instance == null) {
            throw new IllegalStateException("Product service unavailable");
        }
        URI uri = instance.getUri().resolve("/api/product/" + productId);
        try {
            return restClient.get().uri(uri).retrieve().body(Produk.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                return null;
            }
            throw exception;
        }
    }
}
