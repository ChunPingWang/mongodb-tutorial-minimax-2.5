# M14: SAGA Pattern

## 學習目標

實作跨 Aggregate 的分散式交易協調。

## SAGA Pattern 類型

### Choreography SAGA

各服務透過事件協調：

```java
// Order Service
@EventListener
public void handle(OrderCreatedEvent event) {
    // 庫存扣減
    inventoryService.reserveStock(event.getItems());
}
```

### Orchestration SAGA

中央協調者控制流程：

```java
@Service
public class OrderSagaOrchestrator {

    public void executeOrderSaga(CreateOrderCommand command) {
        try {
            // Step 1: 庫存扣減
            inventoryService.reserveStock(command.getItems());
            
            // Step 2: 付款
            paymentService.processPayment(command.getCustomerId(), command.getTotal());
            
            // Step 3: 建立訂單
            orderService.createOrder(command);
            
        } catch (Exception e) {
            // 補償 transaction
            compensate(command);
        }
    }
    
    private void compensate(CreateOrderCommand command) {
        inventoryService.releaseStock(command.getItems());
        paymentService.refund(command.getCustomerId(), command.getTotal());
    }
}
```

### SAGA Log

```java
@Document(collection = "saga_logs")
public class SagaLog {
    @Id
    private ObjectId id;
    
    private String sagaId;
    private String sagaType;
    private SagaStatus status;
    private List<SagaStep> steps;
    private Instant startedAt;
    private Instant completedAt;
}

public class SagaStep {
    private String stepName;
    private String targetService;
    private SagaStepStatus status;
    private String compensatingCommand;
}
```
