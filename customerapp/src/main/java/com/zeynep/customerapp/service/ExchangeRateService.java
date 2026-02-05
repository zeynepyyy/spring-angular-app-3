package com.zeynep.customerapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

    private static final Map<String, BigDecimal> RATES = new HashMap<>();

    static {
        RATES.put("TRY", BigDecimal.ONE);
        RATES.put("USD", new BigDecimal("32.50"));
        RATES.put("EUR", new BigDecimal("35.10"));
        RATES.put("GA", new BigDecimal("7550.00"));
    }

    public Map<String, BigDecimal> getRates() {
        return RATES;
    }

    public BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal amount) {
        BigDecimal fromRate = RATES.get(fromCurrency);
        BigDecimal toRate = RATES.get(toCurrency);

        if (fromRate == null || toRate == null)
            throw new RuntimeException("Geçersiz para birimi!");

        
        return amount.multiply(fromRate).divide(toRate, 2, RoundingMode.HALF_UP);
    }
}
