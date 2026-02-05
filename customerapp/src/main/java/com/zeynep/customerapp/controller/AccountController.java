package com.zeynep.customerapp.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zeynep.customerapp.model.Account;
import com.zeynep.customerapp.model.TransactionLog;
import com.zeynep.customerapp.repository.TransactionLogRepository;
import com.zeynep.customerapp.service.AccountService;
import com.zeynep.customerapp.service.BankDataService;
import com.zeynep.customerapp.service.InterestService;
import com.zeynep.customerapp.service.ReportService;;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {

    private final AccountService accountService;
    private final ReportService reportService;
    private final InterestService interestService;
    private final BankDataService bankDataService;
    private final TransactionLogRepository transactionLogRepository;

    public AccountController(AccountService accountService, ReportService reportService,
            InterestService interestService, BankDataService bankDataService,
            TransactionLogRepository transactionLogRepository) {
        this.accountService = accountService;
        this.reportService = reportService;
        this.interestService = interestService;
        this.bankDataService = bankDataService;
        this.transactionLogRepository = transactionLogRepository;
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @GetMapping("/customer/{customerId}")
    public List<Account> getAccountsByCustomerId(@PathVariable Long customerId) {

        return accountService.getAccountsByCustomerId(customerId);
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
            @RequestParam BigDecimal amount) {
        System.out.println(
                "Transfer Request: From=" + fromAccountNumber + ", To=" + toAccountNumber + ", Amount=" + amount);

        String cleanFrom = fromAccountNumber.replaceAll("\\s+", "");
        String cleanTo = toAccountNumber.replaceAll("\\s+", "");

        System.out.println("Cleaned Accounts: From=" + cleanFrom + ", To=" + cleanTo);

        accountService.transferMoney(cleanFrom, cleanTo, amount);
        return "Transfer başarıyla tamamlandı!";
    }

    @PostMapping("/{id}/process")
    public String processTransaction(@PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam String category) {
        accountService.processSecureTransaction(id, amount, category);
        return "İşlem başarıyla kategoriye (" + category + ") işlendi.";
    }

    @GetMapping("/{id}/report")
    public Map<String, BigDecimal> getReport(@PathVariable Long id) {
        return reportService.getExpenseSummary(id);
    }

    @GetMapping("/{id}/simulate-interest")
    public String simulate(@PathVariable Long id, @RequestParam double rate, @RequestParam int days) {
        Account account = accountService.getAccountById(id);
        BigDecimal netGain = interestService.calculateNetInterest(account.getBalance(), rate, days);

        return days + " gün sonunda %" + rate + " faiz ile net kazancınız (%10 Vergi Kesintisi Dahil): " + netGain
                + " TL";
    }

    @PostMapping("/backup")
    public String backup() {
        return bankDataService.backupData();
    }

    @GetMapping("/{id}/goal-status")
    public String getGoalStatus(@PathVariable Long id) {
        return reportService.getGoalProgress(id);
    }

    @GetMapping("/{accountId}/transactions")
    public List<TransactionLog> getTransactionsByAccountId(@PathVariable Long accountId) {
        return transactionLogRepository.findByFromAccountIdOrToAccountIdOrderByTransactionDateDesc(accountId,
                accountId);
    }

    @PostMapping("/qr")
    public String processQR(@RequestParam Long accountId, @RequestParam BigDecimal amount,
            @RequestParam String action) {
        // action: "WITHDRAW" or "DEPOSIT"
        BigDecimal finalAmount = "WITHDRAW".equalsIgnoreCase(action) ? amount.negate() : amount;

        accountService.processSecureTransaction(accountId, finalAmount, "QR-ATM");
        return "QR İşlemi Başarılı! (" + action + ": " + amount + " TL)";
    }
}
