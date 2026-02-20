package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String eventId,
    String aggregateId,
    Instant timestamp,
    int version,
    String accountHolderName,
    String accountNumber,
    String initialBalance
) implements DomainEvent {

    public AccountOpenedEvent(String accountHolderName, String accountNumber, BigDecimal initialBalance) {
        this(
            UUID.randomUUID().toString(),
            accountNumber,
            Instant.now(),
            1,
            accountHolderName,
            accountNumber,
            initialBalance.toPlainString()
        );
    }
}
