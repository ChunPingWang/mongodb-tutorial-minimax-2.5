package com.example.domain;

import java.time.Instant;
import java.util.UUID;

public sealed interface DomainEvent permits AccountOpenedEvent, DomainEvent.AccountCreditedEvent, 
    DomainEvent.AccountDebitedEvent, DomainEvent.AccountClosedEvent {

    String eventId();

    String aggregateId();

    Instant timestamp();

    int version();

    record AccountCreditedEvent(
        String eventId,
        String aggregateId,
        Instant timestamp,
        int version,
        String amount,
        String description
    ) implements DomainEvent {
        public AccountCreditedEvent(String aggregateId, String amount, String description) {
            this(UUID.randomUUID().toString(), aggregateId, Instant.now(), 0, amount, description);
        }
    }

    record AccountDebitedEvent(
        String eventId,
        String aggregateId,
        Instant timestamp,
        int version,
        String amount,
        String description
    ) implements DomainEvent {
        public AccountDebitedEvent(String aggregateId, String amount, String description) {
            this(UUID.randomUUID().toString(), aggregateId, Instant.now(), 0, amount, description);
        }
    }

    record AccountClosedEvent(
        String eventId,
        String aggregateId,
        Instant timestamp,
        int version,
        String reason
    ) implements DomainEvent {
        public AccountClosedEvent(String aggregateId, String reason) {
            this(UUID.randomUUID().toString(), aggregateId, Instant.now(), 0, reason);
        }
    }
}
