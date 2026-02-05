package com.zeynep.customerapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class InterestService {

    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    public BigDecimal calculateNetInterest(BigDecimal balance, double annualRate, int days) {
        
        BigDecimal dailyRate = BigDecimal.valueOf(annualRate).divide(new BigDecimal("36500"), 10, RoundingMode.HALF_UP);
        BigDecimal grossInterest = balance.multiply(dailyRate).multiply(BigDecimal.valueOf(days));

        
        BigDecimal taxAmount = grossInterest.multiply(TAX_RATE);

        
        return grossInterest.subtract(taxAmount).setScale(2, RoundingMode.HALF_UP);
    }
}