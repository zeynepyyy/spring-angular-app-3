package com.zeynep.customerapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zeynep.customerapp.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    java.util.Optional<Customer> findByEmail(String email);
}
