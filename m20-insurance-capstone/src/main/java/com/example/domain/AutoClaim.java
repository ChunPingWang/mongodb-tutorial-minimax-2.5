package com.example.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "claims")
public non-sealed class AutoClaim implements Claim {

    @Id
    private String id;

    private String claimNumber;
    private String claimantId;
    private String status;
    private BigDecimal claimAmount;
    private Instant filedAt;
    private String description;

    private String vehicleId;
    private String accidentLocation;
    private String policeReportNumber;
    private boolean airbagDeployed;
    private String damageSeverity;

    public AutoClaim() {
    }

    public AutoClaim(String claimNumber, String claimantId, BigDecimal claimAmount, 
                    String description, String vehicleId, String accidentLocation) {
        this.claimNumber = claimNumber;
        this.claimantId = claimantId;
        this.claimAmount = claimAmount;
        this.description = description;
        this.vehicleId = vehicleId;
        this.accidentLocation = accidentLocation;
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getAccidentLocation() {
        return accidentLocation;
    }

    public void setAccidentLocation(String accidentLocation) {
        this.accidentLocation = accidentLocation;
    }

    public String getPoliceReportNumber() {
        return policeReportNumber;
    }

    public void setPoliceReportNumber(String policeReportNumber) {
        this.policeReportNumber = policeReportNumber;
    }

    public boolean isAirbagDeployed() {
        return airbagDeployed;
    }

    public void setAirbagDeployed(boolean airbagDeployed) {
        this.airbagDeployed = airbagDeployed;
    }

    public String getDamageSeverity() {
        return damageSeverity;
    }

    public void setDamageSeverity(String damageSeverity) {
        this.damageSeverity = damageSeverity;
    }
}
