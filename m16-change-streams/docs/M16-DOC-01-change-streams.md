# M16: Change Streams 與事件驅動

## 學習目標

使用 Change Streams 實作即時資料同步與事件驅動。

## Change Streams 原理

### 基本使用

```java
@Configuration
public class ChangeStreamConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public ChangeStreamTask changeStreamTask() {
        return new ChangeStreamTask(mongoTemplate);
    }
}

public class ChangeStreamTask {

    public void watchCollection() {
        ChangeStreamOptions options = ChangeStreamOptions.builder()
            .filter(Aggregation.newAggregation(
                match(Criteria.where("operationType").in("insert", "update", "replace"))
            ))
            .build();

        Flux<ChangeStreamEvent<Document>> changeStream = 
            mongoTemplate.changeStream("accounts", options, Document.class);

        changeStream.subscribe(event -> {
            System.out.println("Operation: " + event.getOperationType());
            System.out.println("Full Document: " + event.getFullDocument());
        });
    }
}
```

### Resume Token 容錯

```java
public void watchWithResume(String resumeToken) {
    ChangeStreamOptions options = ChangeStreamOptions.builder()
        .filter(match)
        .resumeToken(resumeToken)
        .build();
    
    // 從上次中斷處繼續監聽
}
```
