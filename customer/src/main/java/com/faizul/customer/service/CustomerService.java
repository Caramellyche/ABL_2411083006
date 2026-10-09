package com.faizul.customer.service;

import com.faizul.customer.entity.Customer;
import com.faizul.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customer) {
        Customer existingCustomer = customerRepository.findById(id).orElse(null);
        if (existingCustomer == null) {
            return null;
        }

        existingCustomer.setNama(customer.getNama());
        existingCustomer.setAlamat(customer.getAlamat());
        existingCustomer.setJenisKelamin(customer.getJenisKelamin());
        return customerRepository.save(existingCustomer);
    }

    public boolean deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            return false;
        }

        customerRepository.deleteById(id);
        return true;
    }
}