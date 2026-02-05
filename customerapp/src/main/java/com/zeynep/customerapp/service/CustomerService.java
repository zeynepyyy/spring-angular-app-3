package com.zeynep.customerapp.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.zeynep.customerapp.model.Customer;
import com.zeynep.customerapp.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer saveCustomer(Customer customer) {

        Objects.requireNonNull(customer, "Customer nesnesi boş olamaz!");
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer login(String email, String password) {
        return customerRepository.findByEmail(email)
                .filter(customer -> customer.getPassword() != null && customer.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Geçersiz e-posta veya şifre!"));
    }
}
