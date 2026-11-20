package com.faizul.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.faizul.customer.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
