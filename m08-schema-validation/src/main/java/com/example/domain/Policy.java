package com.example.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "policies")
public class Policy {
    @Id
    private String id;

    @NotBlank(message = "Policy number is required")
    @Pattern(regexp = "^POL-\\d{6}$", message = "Policy number must be in format POL-XXXXXX")
    private String policyNumber;

    @NotBlank(message = "Holder name is required")
    private String holderName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Premium amount is required")
    @Min(value = 0, message = "Premium must be non-negative")
    private Double premium;

    @NotNull(message = "Coverage amount is required")
    @Min(value = 1000, message = "Coverage must be at least 1000")
    private Double coverageAmount;

    @NotBlank(message = "Policy type is required")
    private String policyType;

    @NotNull(message = "Start date is required")
    @Past(message = "Start date must be in the past")
    private Instant startDate;

    private Instant endDate;

    @NotNull(message = "Status is required")
    private PolicyStatus status;

    private List<String> beneficiaries;

    private PolicyDetail details;

    public Policy() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Double getPremium() { return premium; }
    public void setPremium(Double premium) { this.premium = premium; }
    public Double getCoverageAmount() { return coverageAmount; }
    public void setCoverageAmount(Double coverageAmount) { this.coverageAmount = coverageAmount; }
    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }
    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }
    public List<String> getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(List<String> beneficiaries) { this.beneficiaries = beneficiaries; }
    public PolicyDetail getDetails() { return details; }
    public void setDetails(PolicyDetail details) { this.details = details; }

    public enum PolicyStatus {
        ACTIVE, EXPIRED, CANCELLED, PENDING
    }

    public static class PolicyDetail {
        private String planName;
        private Integer durationMonths;
        private String paymentFrequency;

        public String getPlanName() { return planName; }
        public void setPlanName(String planName) { this.planName = planName; }
        public Integer getDurationMonths() { return durationMonths; }
        public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
        public String getPaymentFrequency() { return paymentFrequency; }
        public void setPaymentFrequency(String paymentFrequency) { this.paymentFrequency = paymentFrequency; }
    }
}
