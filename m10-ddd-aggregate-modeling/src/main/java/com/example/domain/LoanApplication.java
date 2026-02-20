package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LoanApplication {

    private String id;
    private Applicant applicant;
    private BigDecimal amount;
    private String purpose;
    private LoanStatus status;
    private List<String> approvalSteps;
    private Instant createdAt;
    private Instant updatedAt;
    private String assignedOfficerId;

    public LoanApplication(Applicant applicant, BigDecimal amount, String purpose) {
        this.id = UUID.randomUUID().toString();
        this.applicant = Objects.requireNonNull(applicant);
        this.amount = Objects.requireNonNull(amount);
        this.purpose = Objects.requireNonNull(purpose);
        this.status = LoanStatus.DRAFT;
        this.approvalSteps = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public LoanApplication(String id, Applicant applicant, BigDecimal amount, String purpose,
                          LoanStatus status, List<String> approvalSteps, Instant createdAt,
                          Instant updatedAt, String assignedOfficerId) {
        this.id = id;
        this.applicant = applicant;
        this.amount = amount;
        this.purpose = purpose;
        this.status = status;
        this.approvalSteps = new ArrayList<>(approvalSteps);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.assignedOfficerId = assignedOfficerId;
    }

    public void submit() {
        if (this.status != LoanStatus.DRAFT) {
            throw new IllegalStateException("Only draft applications can be submitted");
        }
        this.status = LoanStatus.SUBMITTED;
        this.updatedAt = Instant.now();
    }

    public void assignOfficer(String officerId) {
        this.assignedOfficerId = Objects.requireNonNull(officerId);
        this.updatedAt = Instant.now();
    }

    public void addApprovalStep(String step) {
        this.approvalSteps.add(step);
        this.updatedAt = Instant.now();
    }

    public void approve() {
        if (this.status != LoanStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted applications can be approved");
        }
        this.status = LoanStatus.APPROVED;
        this.updatedAt = Instant.now();
    }

    public void reject(String reason) {
        if (this.status != LoanStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted applications can be rejected");
        }
        this.status = LoanStatus.REJECTED;
        this.addApprovalStep("REJECTED: " + reason);
        this.updatedAt = Instant.now();
    }

    public void disburse() {
        if (this.status != LoanStatus.APPROVED) {
            throw new IllegalStateException("Only approved applications can be disbursed");
        }
        this.status = LoanStatus.DISBURSED;
        this.updatedAt = Instant.now();
    }

    public boolean isApproved() {
        return this.status == LoanStatus.APPROVED || this.status == LoanStatus.DISBURSED;
    }

    public boolean requiresManualReview() {
        return this.amount.compareTo(new BigDecimal("100000")) > 0;
    }

    public String getId() {
        return id;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public List<String> getApprovalSteps() {
        return List.copyOf(approvalSteps);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getAssignedOfficerId() {
        return assignedOfficerId;
    }

    public enum LoanStatus {
        DRAFT,
        SUBMITTED,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        DISBURSED,
        CLOSED
    }
}
