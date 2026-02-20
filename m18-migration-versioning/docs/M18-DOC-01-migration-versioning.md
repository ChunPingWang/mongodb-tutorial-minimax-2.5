# M18: Schema Migration 與版本管理

## 學習目標

管理 MongoDB Schema 的版本演進。

## Migration 策略

### Mongock 使用

```java
@ChangeUnit(order = "1", id = "add-riskScore")
public class AddRiskScoreChange {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.updateMulti(
            new Query(),
            new Update().set("riskScore", "UNRATED"),
            "policies"
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.updateMulti(
            new Query().addCriteria(Criteria.where("riskScore").exists(true)),
            new Update().unset("riskScore"),
            "policies"
        );
    }
}
```

### Converter Chain

```java
@Configuration
public class DocumentMigrator {

    public Document migrate(Document doc) {
        int version = doc.getInteger("_schemaVersion", 1);
        
        if (version < 2) {
            doc = migrateV1ToV2(doc);
        }
        if (version < 3) {
            doc = migrateV2ToV3(doc);
        }
        
        doc.put("_schemaVersion", 3);
        return doc;
    }
}
```
