# M10: DDD Aggregate 建模

## 學習目標

將 DDD Aggregate Pattern 落實於 MongoDB Document 設計。

## Aggregate Root 與 MongoDB Collection 映射

### Aggregate 邊界原則

- Aggregate 邊界 = Document 邊界 = Transaction 邊界
- Aggregate 內部資料一致性由 Aggregate Root 維護
- Aggregate 間的最終一致性透過 Domain Event 實現

### Hexagonal Architecture + MongoDB

```
┌─────────────────────────────────────────────────────────┐
│                    Application Layer                   │
│  (Command/Query Handlers, DTOs, Mappers)              │
└─────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────────────────────────────────────┐
│                      Domain Layer                       │
│  ┌─────────────────────────────────────────────────┐  │
│  │              Aggregate Root                      │  │
│  │  - LoanApplication                              │  │
│  │  - Business Rules                               │  │
│  │  - Domain Events                                │  │
│  └─────────────────────────────────────────────────┘  │
│  ┌─────────────────────────────────────────────────┐  │
│  │              Value Objects                       │  │
│  │  - Money, Address, ContactInfo                  │  │
│  └─────────────────────────────────────────────────┘  │
│  ┌─────────────────────────────────────────────────┐  │
│  │              Repository Interface                │  │
│  │  - LoanApplicationRepository (Port)            │  │
│  └─────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                   │
│  ┌─────────────────────────────────────────────────┐  │
│  │              MongoDB Repository                   │  │
│  │  - MongoLoanApplicationRepository (Adapter)     │  │
│  └─────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Rich Domain Model 實踐

```java
@Document(collection = "loan_applications")
public class LoanApplication {  // Aggregate Root
    
    @Id
    private ObjectId id;
    
    private String applicationNumber;
    private LoanApplicationStatus status;
    
    private Applicant applicant;
    private LoanDetails loanDetails;
    
    private List<DomainEvent> domainEvents = new ArrayList<>();
    
    // 業務行為方法
    public void submit() {
        if (this.status != LoanApplicationStatus.DRAFT) {
            throw new IllegalStateException("只有草稿狀態可提交");
        }
        this.status = LoanApplicationStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        
        // 產生 Domain Event
        this.addDomainEvent(new LoanApplicationSubmittedEvent(this.id));
    }
    
    public void approve() {
        validateForApproval();
        this.status = LoanApplicationStatus.APPROVED;
        this.approvedAt = Instant.now();
        
        this.addDomainEvent(new LoanApplicationApprovedEvent(this.id));
    }
    
    private void validateForApproval() {
        if (this.status != LoanApplicationStatus.SUBMITTED) {
            throw new IllegalStateException("只有已提交狀態可審核");
        }
        if (applicant.getAnnualIncome()
            .multiply(BigDecimal.valueOf(3))
            .compareTo(loanDetails.getLoanAmount()) < 0) {
            throw new BusinessRuleViolationException("年收入需為贷款的3倍");
        }
    }
    
    private void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }
}
```

### Repository 介面 (Port)

```java
public interface LoanApplicationRepository {
    Optional<LoanApplication> findById(ObjectId id);
    
    Optional<LoanApplication> findByApplicationNumber(String applicationNumber);
    
    void save(LoanApplication loanApplication);
    
    void delete(LoanApplication loanApplication);
    
    Page<LoanApplication> findByStatus(
        LoanApplicationStatus status, 
        Pageable pageable
    );
}
```

### MongoDB Repository 實作 (Adapter)

```java
@Repository
public class MongoLoanApplicationRepository 
    implements LoanApplicationRepository {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public Optional<LoanApplication> findById(ObjectId id) {
        return Optional.ofNullable(mongoTemplate.findById(id, LoanApplication.class));
    }
    
    @Override
    public Optional<LoanApplication> findByApplicationNumber(String applicationNumber) {
        Query query = Query.query(
            Criteria.where("applicationNumber").is(applicationNumber)
        );
        return Optional.ofNullable(
            mongoTemplate.findOne(query, LoanApplication.class)
        );
    }
    
    @Override
    public void save(LoanApplication loanApplication) {
        if (loanApplication.getId() == null) {
            mongoTemplate.insert(loanApplication);
        } else {
            mongoTemplate.save(loanApplication);
        }
    }
}
```
