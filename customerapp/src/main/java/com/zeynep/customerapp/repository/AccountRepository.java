package com.zeynep.customerapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zeynep.customerapp.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);

    java.util.Optional<Account> findByAccountNumber(String accountNumber);
}
