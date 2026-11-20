package com.faizul.order.client;

import java.net.URI;

import com.faizul.order.vo.Pelanggan;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class CustomerClient {
    private final LoadBalancerClient loadBalancerClient;
    private final RestClient restClient = RestClient.create();

    public CustomerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    public Pelanggan getCustomer(Long customerId) {
        ServiceInstance instance = loadBalancerClient.choose("customer");
        if (instance == null) {
            throw new IllegalStateException("Customer service unavailable");
        }
        URI uri = instance.getUri().resolve("/api/customer/" + customerId);
        try {
            return restClient.get().uri(uri).retrieve().body(Pelanggan.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                return null;
            }
            throw exception;
        }
    }
}
