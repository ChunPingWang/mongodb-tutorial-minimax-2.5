package com.example.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Fund(
    String productId,
    String productName,
    BigDecimal currentValue,
    LocalDate startDate,
    LocalDate endDate,
    String fundManager,
    String fundType,
    BigDecimal navPerUnit,
    BigDecimal totalUnits
) implements FinancialProduct {

    public Fund {
        if (navPerUnit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("NAV per unit must be positive");
        }
    }

    @Override
    public ProductType productType() {
        return ProductType.FUND;
    }

    public BigDecimal calculateTotalValue() {
        return navPerUnit.multiply(totalUnits);
    }

    public BigDecimal calculateReturn() {
        BigDecimal initialValue = navPerUnit.multiply(totalUnits);
        if (initialValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentValue.subtract(initialValue)
            .divide(initialValue, 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    public boolean isOpenEnded() {
        return endDate == null;
    }
}
