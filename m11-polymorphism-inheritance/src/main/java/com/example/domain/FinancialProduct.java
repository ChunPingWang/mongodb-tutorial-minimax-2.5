package com.example.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public sealed interface FinancialProduct
    permits Deposit, Fund, Insurance {

    String productId();

    String productName();

    BigDecimal currentValue();

    LocalDate startDate();

    LocalDate endDate();

    ProductType productType();

    enum ProductType {
        DEPOSIT,
        FUND,
        INSURANCE
    }
}
