package com.example.saga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class OrderSagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaOrchestrator.class);

    private final SagaLogRepository sagaLogRepository;
    private final List<SagaStepHandler> stepHandlers;

    public OrderSagaOrchestrator(SagaLogRepository sagaLogRepository) {
        this.sagaLogRepository = sagaLogRepository;
        this.stepHandlers = new ArrayList<>();
    }

    public void registerStepHandler(SagaStepHandler handler) {
        this.stepHandlers.add(handler);
    }

    public SagaLog executeSaga(String sagaType, String payload, List<SagaStepDefinition> steps) {
        SagaLog sagaLog = new SagaLog(sagaType, payload);
        sagaLogRepository.save(sagaLog);

        try {
            for (int i = 0; i < steps.size(); i++) {
                SagaStepDefinition stepDef = steps.get(i);
                
                sagaLog.addStep(stepDef.stepName(), "STARTED", null);
                sagaLogRepository.save(sagaLog);

                try {
                    Object result = executeStep(stepDef);
                    
                    sagaLog.addStep(stepDef.stepName(), "COMPLETED", stepDef.compensationData());
                    sagaLogRepository.save(sagaLog);
                    
                    log.info("Completed saga step: {} for saga: {}", stepDef.stepName(), sagaLog.getSagaId());
                    
                } catch (Exception e) {
                    log.error("Failed at saga step: {} for saga: {}", stepDef.stepName(), sagaLog.getSagaId(), e);
                    
                    sagaLog.addStep(stepDef.stepName(), "FAILED", stepDef.compensationData());
                    sagaLog.markFailed(e.getMessage());
                    sagaLogRepository.save(sagaLog);
                    
                    compensate(sagaLog, i);
                    return sagaLog;
                }
            }

            sagaLog.markCompleted();
            sagaLogRepository.save(sagaLog);
            log.info("Saga completed successfully: {}", sagaLog.getSagaId());
            
        } catch (Exception e) {
            log.error("Saga execution failed: {}", sagaLog.getSagaId(), e);
            sagaLog.markFailed(e.getMessage());
            sagaLogRepository.save(sagaLog);
        }

        return sagaLog;
    }

    private Object executeStep(SagaStepDefinition stepDef) {
        for (SagaStepHandler handler : stepHandlers) {
            if (handler.canHandle(stepDef.stepName())) {
                return handler.execute(stepDef.payload());
            }
        }
        throw new IllegalStateException("No handler found for step: " + stepDef.stepName());
    }

    private void compensate(SagaLog sagaLog, int failedAtIndex) {
        log.info("Starting compensation for saga: {}", sagaLog.getSagaId());
        
        List<SagaLog.SagaStep> completedSteps = sagaLog.getSteps();
        
        for (int i = failedAtIndex - 1; i >= 0; i--) {
            SagaLog.SagaStep step = completedSteps.get(i);
            String compensationData = step.compensationData();
            
            if (compensationData != null) {
                try {
                    executeCompensation(step.stepName(), compensationData);
                    sagaLog.addStep(step.stepName() + "_COMPENSATION", "COMPLETED", null);
                    log.info("Compensated step: {}", step.stepName());
                } catch (Exception e) {
                    log.error("Compensation failed for step: {}", step.stepName(), e);
                    sagaLog.addStep(step.stepName() + "_COMPENSATION", "FAILED", null);
                }
            }
        }

        sagaLog.markCompensated();
        sagaLogRepository.save(sagaLog);
    }

    private void executeCompensation(String stepName, String compensationData) {
        for (SagaStepHandler handler : stepHandlers) {
            if (handler.canHandle(stepName + "_COMPENSATION")) {
                handler.compensate(compensationData);
                return;
            }
        }
        log.warn("No compensation handler found for step: {}", stepName);
    }

    public record SagaStepDefinition(
        String stepName,
        String payload,
        String compensationData
    ) {
    }

    public interface SagaStepHandler {
        boolean canHandle(String stepName);
        Object execute(String payload);
        void compensate(String compensationData);
    }

    public interface SagaLogRepository {
        SagaLog save(SagaLog sagaLog);
    }
}
