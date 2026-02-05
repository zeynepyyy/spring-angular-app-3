package com.zeynep.customerapp.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zeynep.customerapp.service.AccountService;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "http://localhost:4200")
public class LoanController {

    private final AccountService accountService;
    private final com.zeynep.customerapp.service.FuzzyScoringService fuzzyService;
    private final com.zeynep.customerapp.repository.CustomerRepository customerRepository;

    public LoanController(AccountService accountService,
            com.zeynep.customerapp.service.FuzzyScoringService fuzzyService,
            com.zeynep.customerapp.repository.CustomerRepository customerRepository) {
        this.accountService = accountService;
        this.fuzzyService = fuzzyService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/apply")
    public String applyForLoan(@RequestParam Long customerId,
            @RequestParam BigDecimal amount,
            @RequestParam int months) {
        accountService.createLoanAccount(customerId, amount, months);
        return "Kredi başvurunuz onaylandı! " + amount + " TL hesabınıza aktarıldı.";
    }

    @org.springframework.web.bind.annotation.GetMapping("/limit/{customerId}")
    public BigDecimal getLoanLimit(@org.springframework.web.bind.annotation.PathVariable Long customerId) {
        Double score = customerRepository.findById(customerId).map(c -> c.getCreditScore()).orElse(0.0);
        return fuzzyService.calculateMaxLoanLimit(score);
    }

    @PostMapping("/repay")
    public String repayLoan(@RequestParam Long loanAccountId,
            @RequestParam Long sourceAccountId,
            @RequestParam BigDecimal amount) {
        accountService.payLoan(loanAccountId, sourceAccountId, amount);
        return "Ödeme başarıyla alındı. Teşekkürler!";
    }
}
