# M12: Event Sourcing with MongoDB

## 學習目標

使用 MongoDB 實作 Event Sourcing Pattern。

## Event Store 設計

### Events Collection 結構

```java
@Document(collection = "events")
public class StoredEvent {
    @Id
    private ObjectId id;
    
    private String aggregateId;
    private String aggregateType;
    private String eventType;
    private int version;
    private Instant timestamp;
    private Document payload;
    
    private Document metadata;
}
```

### Aggregate Event Store

```java
public interface EventStore {
    void append(DomainEvent event, String aggregateId, String aggregateType, int version);
    
    List<DomainEvent> getEventsForAggregate(String aggregateId);
    
    void saveSnapshot(String aggregateId, Object state, int version);
    
    Optional<Object> getLatestSnapshot(String aggregateId);
}
```

### Event Replay

```java
@Service
public class AccountEventService {

    public BankAccount reconstructAggregate(String accountId) {
        List<DomainEvent> events = eventStore.getEventsForAggregate(accountId);
        
        BankAccount account = new BankAccount();
        for (DomainEvent event : events) {
            account.apply(event);  // Replay events
        }
        return account;
    }
}
```
