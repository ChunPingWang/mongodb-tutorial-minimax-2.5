package com.example.saga;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SagaLog {

    private String id;
    private String sagaId;
    private String sagaType;
    private SagaStatus status;
    private String payload;
    private List<SagaStep> steps;
    private Instant startedAt;
    private Instant completedAt;
    private String errorMessage;

    public SagaLog(String sagaType, String payload) {
        this.id = UUID.randomUUID().toString();
        this.sagaId = UUID.randomUUID().toString();
        this.sagaType = sagaType;
        this.payload = payload;
        this.status = SagaStatus.IN_PROGRESS;
        this.steps = new ArrayList<>();
        this.startedAt = Instant.now();
    }

    public void addStep(String stepName, String status, String compensationData) {
        SagaStep step = new SagaStep(stepName, status, compensationData);
        this.steps.add(step);
    }

    public void markCompleted() {
        this.status = SagaStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void markFailed(String errorMessage) {
        this.status = SagaStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }

    public void compensate() {
        this.status = SagaStatus.COMPENSATING;
    }

    public void markCompensated() {
        this.status = SagaStatus.COMPENSATED;
        this.completedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getSagaId() {
        return sagaId;
    }

    public String getSagaType() {
        return sagaType;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public String getPayload() {
        return payload;
    }

    public List<SagaStep> getSteps() {
        return List.copyOf(steps);
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public record SagaStep(
        String stepName,
        String status,
        String compensationData
    ) {
    }

    public enum SagaStatus {
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        COMPENSATING,
        COMPENSATED
    }
}
