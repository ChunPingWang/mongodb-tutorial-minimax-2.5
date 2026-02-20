# M13: CQRS Read Model

## 學習目標

實作 Command/Query 分離，優化讀取效能。

## CQRS 架構

### Write Model vs Read Model

```
┌──────────────────────────────────────────────────────────────────┐
│                        Command Side                               │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │  Command     │───▶│  Aggregate   │───▶│ Event Store   │     │
│  │  Handler     │    │  (Domain)    │    │ (MongoDB)    │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
└──────────────────────────────────────────────────────────────────┘
                                    │
                                    │ Domain Events
                                    ▼
┌──────────────────────────────────────────────────────────────────┐
│                         Query Side                               │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │   Query      │───▶│ Projection   │───▶│  Read Model  │     │
│  │   Handler    │    │  Handler     │    │  (MongoDB)   │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
└──────────────────────────────────────────────────────────────────┘
```

### 客戶 360 Read Model

```java
@Document(collection = "customer_summary_views")
public class CustomerSummaryView {
    @Id
    private ObjectId id;
    
    private String customerId;
    private String customerName;
    private String customerTier;
    private boolean isVip;
    
    private BigDecimal totalDeposit;
    private BigDecimal totalInvestment;
    private BigDecimal totalLoan;
    private BigDecimal creditLimit;
    
    private List<RecentTransaction> recentTransactions;
}
```

### Projection Handler

```java
@Component
public class CustomerProjectionHandler {

    @Autowired
    private MongoTemplate mongoTemplate;

    @EventListener
    public void handle(AccountOpenedEvent event) {
        Query query = Query.query(
            Criteria.where("customerId").is(event.getCustomerId())
        );
        
        Update update = new Update()
            .inc("totalDeposit", event.getInitialBalance());
        
        mongoTemplate.upsert(query, update, CustomerSummaryView.class);
    }
}
```
