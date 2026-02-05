package com.zeynep.customerapp.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeynep.customerapp.model.Account;
import com.zeynep.customerapp.model.Customer;
import com.zeynep.customerapp.model.TransactionLog;
import com.zeynep.customerapp.repository.AccountRepository;
import com.zeynep.customerapp.repository.TransactionLogRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionLogRepository transactionLogRepository;
    private final FuzzyScoringService fuzzyScoringService;

    public AccountService(AccountRepository accountRepository,
            TransactionLogRepository transactionLogRepository,
            FuzzyScoringService fuzzyScoringService) {
        this.accountRepository = accountRepository;
        this.transactionLogRepository = transactionLogRepository;
        this.fuzzyScoringService = fuzzyScoringService;
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    // ... (Existing methods kept as is via context, but for replace_file_content I
    // need to target specific blocks if they are far apart)
    // Actually, createLoanAccount is at the end. I should use multi_replace for
    // this file if I want to touch both constructor and the method.
    // However, I can just do two replacements or one big one if I include the
    // middle. The file is small enough (136 lines).
    // Let's do multi_replace.

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı! ID: " + id));
    }

    public List<Account> getAllAccounts() {

        return accountRepository.findAll();
    }

    @Transactional
    public synchronized void transferMoney(Long fromId, Long toId, BigDecimal amount) { // Keep old method for
                                                                                        // compatibility if needed or
                                                                                        // deprecate
        transferMoney(getAccountById(fromId).getAccountNumber(), getAccountById(toId).getAccountNumber(), amount);
    }

    @Transactional
    public synchronized void transferMoney(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account from = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Gönderen hesap bulunamadı! Hesap No: " + fromAccountNumber));
        Account to = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Alıcı hesap bulunamadı! Hesap No: " + toAccountNumber));

        if (from.getId().equals(to.getId())) {
            throw new RuntimeException("Kendinize transfer yapamazsınız!");
        }

        if (!"KREDI".equalsIgnoreCase(from.getAccountType())) {
            if (from.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Yetersiz bakiye!");
            }
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        saveLog(from.getId(), to.getId(), amount, "Transfer -> " + toAccountNumber);
    }

    @Transactional
    public synchronized void processSecureTransaction(Long accountId, BigDecimal amount, String category) {
        Account account = getAccountById(accountId);
        String type = account.getAccountType() != null ? account.getAccountType().toUpperCase() : "SAVINGS";

        if ("VADELI".equals(type) && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Vade dolmadan vadeli hesaptan para çekilemez!");
        }

        if ("HEDEF".equals(type) && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Hedefine ulaşmadan para çekemezsin!");
        }

        if (!"KREDI".equals(type)) {
            if (amount.compareTo(BigDecimal.ZERO) < 0 && account.getBalance().compareTo(amount.abs()) < 0) {
                throw new RuntimeException("Yetersiz bakiye! " + type + " hesabı eksiye düşemez.");
            }
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        saveLog(accountId, null, amount, category.toUpperCase() + " Isleri");
    }

    @Transactional
    public void processBillPayment(Long accountId, BigDecimal amount, String billType) {
        Account account = getAccountById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz bakiye!");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        saveLog(accountId, null, amount.negate(), billType + " Fatura Ödemesi");
    }

    private void saveLog(Long fromId, Long toId, BigDecimal amount, String description) {
        TransactionLog log = new TransactionLog();
        log.setFromAccountId(fromId);
        log.setToAccountId(toId);
        log.setAmount(amount);
        log.setDescription(description);
        transactionLogRepository.save(log);
    }

    public List<Account> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @Transactional
    public void createLoanAccount(Long customerId, BigDecimal amount, int months) {
        // Müşteriyi bul (Limit kontrolü için)
        List<Account> existingAccounts = getAccountsByCustomerId(customerId);
        if (existingAccounts.isEmpty()) {
            throw new RuntimeException("Müşteri bulunamadı!");
        }
        Customer customer = existingAccounts.get(0).getCustomer();

        // 0. Fuzzy Limit Kontrolü
        BigDecimal maxLimit = fuzzyScoringService.calculateMaxLoanLimit(customer.getCreditScore());
        if (amount.compareTo(maxLimit) > 0) {
            throw new RuntimeException("Kredi skorunuz (" + customer.getCreditScore()
                    + ") bu tutar için yetersiz! Maksimum çekebileceğiniz tutar: " + maxLimit + " TL");
        }

        // 1. Faiz Oranı ve Toplam Geri Ödeme Hesaplama
        BigDecimal interestRate = new BigDecimal("1.25"); // %1.25 Aylık Faiz
        BigDecimal totalRepayment = amount.multiply(interestRate).add(amount);

        // 2. Kredi Hesabı Oluştur (Borç olarak eksi bakiye)
        Account loanAccount = new Account();
        loanAccount.setAccountNumber("LOAN-" + System.currentTimeMillis());
        loanAccount.setAccountType("KREDI");
        loanAccount.setBalance(totalRepayment.negate());
        loanAccount.setGoalAmount(BigDecimal.ZERO);
        loanAccount.setCustomer(customer); // Müşteriyi direk atıyoruz

        accountRepository.save(loanAccount);

        // 3. Parayı Vadesiz Hesaba Yatır
        Account mainAccount = existingAccounts.stream()
                .filter(acc -> "VADESIZ".equalsIgnoreCase(acc.getAccountType()))
                .findFirst()
                .orElse(existingAccounts.get(0));

        mainAccount.setBalance(mainAccount.getBalance().add(amount));
        accountRepository.save(mainAccount);

        // Log al
        saveLog(loanAccount.getId(), mainAccount.getId(), amount, months + " Ay Vadeli Kredi Kullanımı");
    }

    @Transactional
    public synchronized void payLoan(Long loanAccountId, Long sourceAccountId, BigDecimal amount) {
        Account loanAccount = getAccountById(loanAccountId);
        Account sourceAccount = getAccountById(sourceAccountId);

        if (!"KREDI".equals(loanAccount.getAccountType())) {
            throw new RuntimeException("Hedef hesap bir kredi hesabı değil!");
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Ödeme yapacak hesapta yeterli bakiye yok!");
        }

        // 1. Kaynak hesaptan düş
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));

        // 2. Kredi hesabına ekle (Borç olduğu için balance artacak, sıfıra yaklaşacak)
        loanAccount.setBalance(loanAccount.getBalance().add(amount));

        // Eğer borçtan fazla ödenirse bakiye artıya geçebilir (Opsiyonel: Bunu
        // engelleyebiliriz veya iade sayabiliriz)
        // Şimdilik izin veriyoruz.

        accountRepository.save(sourceAccount);
        accountRepository.save(loanAccount);

        saveLog(sourceAccountId, loanAccountId, amount, "Kredi Ödemesi");
    }
}