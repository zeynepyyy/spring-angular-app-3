package com.zeynep.customerapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zeynep.customerapp.model.Bill;
import com.zeynep.customerapp.service.BillService;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:4200")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/{customerId}")
    public List<Bill> getBills(@PathVariable Long customerId) {
        return billService.getBills(customerId);
    }

    @PostMapping("/pay/{billId}")
    public String payBill(@PathVariable Long billId, @RequestParam Long accountId) {
        billService.payBill(billId, accountId);
        return "Fatura başarıyla ödendi!";
    }

    @PostMapping("/create")
    public Bill createBill(@RequestBody Bill bill) {
        return billService.createBill(bill);
    }
}
