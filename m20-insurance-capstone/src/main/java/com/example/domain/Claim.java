package com.example.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "claims")
public sealed interface Claim permits AutoClaim, HealthClaim {

    @Id
    String getId();

    String getClaimNumber();

    String getClaimantId();

    String getStatus();

    BigDecimal getClaimAmount();

    Instant getFiledAt();

    String getDescription();

    record ClaimDetails(
        String id,
        String claimNumber,
        String claimantId,
        String status,
        BigDecimal claimAmount,
        Instant filedAt,
        String description
    ) {}
}
