package com.example.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "claims")
public non-sealed class HealthClaim implements Claim {

    @Id
    private String id;

    private String claimNumber;
    private String claimantId;
    private String status;
    private BigDecimal claimAmount;
    private Instant filedAt;
    private String description;

    private String providerId;
    private String procedureCode;
    private String diagnosisCode;
    private Instant serviceDate;
    private boolean preApproved;

    public HealthClaim() {
    }

    public HealthClaim(String claimNumber, String claimantId, BigDecimal claimAmount,
                      String description, String providerId, String procedureCode) {
        this.claimNumber = claimNumber;
        this.claimantId = claimantId;
        this.claimAmount = claimAmount;
        this.description = description;
        this.providerId = providerId;
        this.procedureCode = procedureCode;
        this.status = "SUBMITTED";
        this.filedAt = Instant.now();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getClaimNumber() {
        return claimNumber;
    }

    @Override
    public String getClaimantId() {
        return claimantId;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    @Override
    public Instant getFiledAt() {
        return filedAt;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public void setClaimantId(String claimantId) {
        this.claimantId = claimantId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public void setFiledAt(Instant filedAt) {
        this.filedAt = filedAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public Instant getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Instant serviceDate) {
        this.serviceDate = serviceDate;
    }

    public boolean isPreApproved() {
        return preApproved;
    }

    public void setPreApproved(boolean preApproved) {
        this.preApproved = preApproved;
    }
}
