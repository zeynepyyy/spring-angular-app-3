package com.zeynep.customerapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zeynep.customerapp.model.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByCustomerId(Long customerId);
}
