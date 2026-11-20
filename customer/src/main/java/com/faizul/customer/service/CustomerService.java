package com.faizul.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.faizul.customer.client.ProductClient;
import com.faizul.customer.entity.Customer;
import com.faizul.customer.repository.CustomerRepository;
import com.faizul.customer.vo.Produk;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ProductClient productClient;

    public CustomerService(CustomerRepository customerRepository, ProductClient productClient) {
        this.customerRepository = customerRepository;
        this.productClient = productClient;
    }

    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customer) {
        Customer existing = customerRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setNama(customer.getNama());
        existing.setEmail(customer.getEmail());
        existing.setNomorTelepon(customer.getNomorTelepon());
        existing.setAlamat(customer.getAlamat());
        return customerRepository.save(existing);
    }

    public boolean deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            return false;
        }
        customerRepository.deleteById(id);
        return true;
    }

    /**
     * Hubungkan pelanggan dengan produk dari product service.
     * Verifikasi produk lewat panggilan antar-service (Eureka).
     * @return Pelanggan yang sudah diperbarui, atau null jika pelanggan/produk tidak ada.
     */
    public Customer addProduct(Long customerId, Long productId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return null;
        }

        Produk produk = productClient.getProduk(productId);
        if (produk == null) {
            return null;
        }

        if (!customer.getProductIds().contains(productId)) {
            customer.getProductIds().add(productId);
        }
        return customerRepository.save(customer);
    }

    /**
     * Ambil pelanggan beserta detail produk (dari product service).
     */
    public CustomerDetail getDetail(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return null;
        }

        java.util.List<Produk> produkList = new java.util.ArrayList<>();
        for (Long productId : customer.getProductIds()) {
            Produk produk = productClient.getProduk(productId);
            if (produk != null) {
                produkList.add(produk);
            }
        }
        return new CustomerDetail(customer, produkList);
    }

    public record CustomerDetail(Customer customer, java.util.List<Produk> produk) {
    }
}
