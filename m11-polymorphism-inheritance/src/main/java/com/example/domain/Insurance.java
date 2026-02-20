package com.example.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Insurance(
    String productId,
    String productName,
    BigDecimal currentValue,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal sumAssured,
    BigDecimal premiumAmount,
    String policyNumber,
    String coverageType
) implements FinancialProduct {

    public Insurance {
        if (sumAssured.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sum assured must be positive");
        }
    }

    @Override
    public ProductType productType() {
        return ProductType.INSURANCE;
    }

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public long daysRemaining() {
        LocalDate now = LocalDate.now();
        if (now.isAfter(endDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(now, endDate);
    }

    public BigDecimal calculateDeathBenefit() {
        return sumAssured.add(currentValue);
    }

    public boolean isLifeInsurance() {
        return "LIFE".equalsIgnoreCase(coverageType);
    }
}
