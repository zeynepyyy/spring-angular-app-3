package com.zeynep.customerapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zeynep.customerapp.model.Account;
import com.zeynep.customerapp.model.TransactionLog;
import com.zeynep.customerapp.repository.AccountRepository;
import com.zeynep.customerapp.repository.TransactionLogRepository;

@Service
public class ReportService {

    private final TransactionLogRepository logRepository;
    private final AccountRepository accountRepository;

    public ReportService(TransactionLogRepository logRepository, AccountRepository accountRepository) {
        this.logRepository = logRepository;
        this.accountRepository = accountRepository;
    }

    public Map<String, BigDecimal> getExpenseSummary(Long accountId) {
        List<TransactionLog> allLogs = logRepository.findByFromAccountId(accountId);

        return allLogs.stream()
                // Harcama Mantığı:
                // 1. Tutar negatifse (Para çekme/Harcama)
                // 2. Tutar pozitif ama para TRANSFER edilmişse (toAccountId doluysa, para
                // benden gitmiştir)
                .filter(log -> log.getAmount().compareTo(BigDecimal.ZERO) < 0 ||
                        (log.getAmount().compareTo(BigDecimal.ZERO) > 0 && log.getToAccountId() != null))
                .collect(Collectors.groupingBy(
                        TransactionLog::getDescription,
                        Collectors.reducing(BigDecimal.ZERO,
                                log -> log.getAmount().abs(),
                                BigDecimal::add)));
    }

    public String getGoalProgress(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();

        if (!"HEDEF".equalsIgnoreCase(account.getAccountType())) {
            return "Bu bir hedef hesabı değildir.";
        }

        if (account.getGoalAmount() == null || account.getGoalAmount().compareTo(BigDecimal.ZERO) == 0) {
            return "Hedef tutarı belirlenmemiş.";
        }

        BigDecimal progress = account.getBalance()
                .multiply(new BigDecimal("100"))
                .divide(account.getGoalAmount(), 2, RoundingMode.HALF_UP);

        return "Hedefinize %" + progress + " oranında yaklaştınız. (" +
                account.getBalance() + " / " + account.getGoalAmount() + ")";
    }
}
