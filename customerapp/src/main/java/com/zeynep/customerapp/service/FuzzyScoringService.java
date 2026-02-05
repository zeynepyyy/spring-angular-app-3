package com.zeynep.customerapp.service;

import org.springframework.stereotype.Service;

@Service
public class FuzzyScoringService {

    public java.math.BigDecimal calculateMaxLoanLimit(Double creditScore) {
        if (creditScore == null)
            return java.math.BigDecimal.ZERO;

        
        if (creditScore < 600) {
            
            return new java.math.BigDecimal("10000");
        } else if (creditScore < 1000) {
            
            return new java.math.BigDecimal("50000");
        } else {
            
            double bonus = (creditScore - 1000) * 50;
            return new java.math.BigDecimal("100000").add(new java.math.BigDecimal(bonus));
        }
    }
}
