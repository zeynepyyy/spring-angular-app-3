package com.zeynep.customerapp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeynep.customerapp.model.Bill;
import com.zeynep.customerapp.repository.BillRepository;

@Service
public class BillService {
    private final BillRepository billRepository;
    private final AccountService accountService;

    public BillService(BillRepository billRepository, AccountService accountService) {
        this.billRepository = billRepository;
        this.accountService = accountService;
    }

    public List<Bill> getBills(Long customerId) {
        List<Bill> bills = billRepository.findByCustomerId(customerId);
        if (bills.isEmpty()) {
           
            createMockBills(customerId);
            bills = billRepository.findByCustomerId(customerId);
        }
        return bills;
    }

    private void createMockBills(Long customerId) {
        Random rand = new Random();
        billRepository.save(new Bill(customerId, "ELEKTRIK", new BigDecimal(rand.nextInt(300) + 200),
                LocalDate.now().plusDays(5), "UNPAID"));
        billRepository.save(new Bill(customerId, "SU", new BigDecimal(rand.nextInt(100) + 100),
                LocalDate.now().plusDays(10), "UNPAID"));
        billRepository
                .save(new Bill(customerId, "INTERNET", new BigDecimal("450"), LocalDate.now().plusDays(15), "UNPAID"));
    }

    @Transactional
    public void payBill(Long billId, Long accountId) {
        Bill bill = billRepository.findById(billId).orElseThrow(() -> new RuntimeException("Fatura bulunamadı!"));
        if ("PAID".equals(bill.getStatus())) {
            throw new RuntimeException("Bu fatura zaten ödenmiş!");
        }

        // Hesaptan düş ve logla
        accountService.processBillPayment(accountId, bill.getAmount(), bill.getBillType());

        bill.setStatus("PAID");
        billRepository.save(bill);
    }

    public Bill createBill(Bill bill) {
        // Varsayılan değerler
        if (bill.getStatus() == null)
            bill.setStatus("UNPAID");
        if (bill.getDueDate() == null)
            bill.setDueDate(LocalDate.now().plusDays(7));

        return billRepository.save(bill);
    }
}
