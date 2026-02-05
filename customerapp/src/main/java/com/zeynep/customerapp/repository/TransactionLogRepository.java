package com.zeynep.customerapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zeynep.customerapp.model.TransactionLog;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    
    List<TransactionLog> findByFromAccountIdOrToAccountId(Long fromId, Long toId);
    List<TransactionLog> findByFromAccountId(Long fromAccountId);
    List<TransactionLog> findByFromAccountIdOrToAccountIdOrderByTransactionDateDesc(Long fromAccountId, Long toAccountId);
}
