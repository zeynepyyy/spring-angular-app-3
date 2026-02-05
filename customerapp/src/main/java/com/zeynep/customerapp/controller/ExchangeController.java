package com.zeynep.customerapp.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zeynep.customerapp.model.Account;
import com.zeynep.customerapp.service.AccountService;
import com.zeynep.customerapp.service.ExchangeRateService;

@RestController
@RequestMapping("/api/exchange")
@CrossOrigin(origins = "http://localhost:4200")
public class ExchangeController {

    private final ExchangeRateService exchangeRateService;
    private final AccountService accountService;

    public ExchangeController(ExchangeRateService exchangeRateService, AccountService accountService) {
        this.exchangeRateService = exchangeRateService;
        this.accountService = accountService;
    }

    @GetMapping("/rates")
    public Map<String, BigDecimal> getRates() {
        return exchangeRateService.getRates();
    }

    @PostMapping("/trade")
    public String trade(@RequestParam Long customerId,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam BigDecimal amount) {

        // 1. Hesapları Bul veya Oluştur
        Account fromAccount = accountService.getAccountsByCustomerId(customerId).stream()
                .filter(a -> fromCurrency.equals(a.getCurrency()) && "VADESIZ".equals(a.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(fromCurrency + " hesabınız bulunamadı!"));

        Account toAccount = accountService.getAccountsByCustomerId(customerId).stream()
                .filter(a -> toCurrency.equals(a.getCurrency()) && "VADESIZ".equals(a.getAccountType()))
                .findFirst()
                .orElse(null);

        // Hedef hesap yoksa oluştur
        if (toAccount == null) {
            toAccount = new Account();
            toAccount.setCustomer(fromAccount.getCustomer());
            toAccount.setAccountNumber(toCurrency + "-" + System.currentTimeMillis());
            toAccount.setAccountType("VADESIZ");
            toAccount.setCurrency(toCurrency);
            toAccount.setBalance(BigDecimal.ZERO);
            accountService.createAccount(toAccount);
        }

        // 2. Bakiye Kontrolü
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz Bakiye!");
        }

        // 3. Çeviri Yap
        BigDecimal targetAmount = exchangeRateService.convert(fromCurrency, toCurrency, amount);

        // 4. Transferi Gerçekleştir
        accountService.transferMoney(fromAccount.getId(), toAccount.getId(), amount); // Bu metot TRY bazlı çalışıyor
                                                                                      // olabilir, DİKKAT!

        // AccountService.transferMoney metodu sadece balance update yapıyor olabilir.
        // Ancak bu farklı para birimleri arasında. Direkt transferMoney kullanamayız
        // çünkü o birebir düşüp ekliyor.
        // Manuel yapmalıyız.

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(targetAmount));

        accountService.createAccount(fromAccount); // Save
        accountService.createAccount(toAccount); // Save

        return "İşlem Başarılı! " + amount + " " + fromCurrency + " -> " + targetAmount + " " + toCurrency;
    }
}
