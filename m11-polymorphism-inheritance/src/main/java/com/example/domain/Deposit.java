package com.example.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Deposit(
    String productId,
    String productName,
    BigDecimal currentValue,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal interestRate,
    String depositType,
    BigDecimal minimumBalance
) implements FinancialProduct {

    public Deposit {
        if (interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
    }

    @Override
    public ProductType getProductType() {
        return ProductType.DEPOSIT;
    }

    public BigDecimal calculateInterest() {
        return currentValue.multiply(interestRate)
            .multiply(BigDecimal.valueOf(
                java.time.temporal.ChronoUnit.DAYS.between(startDate(), LocalDate.now()) / 365.0
            ));
    }

    public boolean isMatured() {
        return LocalDate.now().isAfter(endDate());
    }

    public long daysToMaturity() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate());
    }
}
