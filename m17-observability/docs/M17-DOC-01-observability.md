# M17: 可觀測性與監控

## 學習目標

建立 MongoDB 應用的完整可觀測性。

## 可觀測性三支柱

### 1. Metrics

```java
@Configuration
public class MongoMetricsConfig {

    @Bean
    public MongoDBConnectionPoolMetrics mongoDBConnectionPoolMetrics(
        MongoDbFactory dbFactory) {
        return new MongoDBConnectionPoolMetrics(
            dbFactory.getMongoDatabase().getCollection("test")
        );
    }
}
```

### 2. Logging

```yaml
logging:
  level:
    org.springframework.data.mongodb: DEBUG
    org.mongodb.driver: DEBUG
```

### 3. Tracing

```java
@Bean
public MongoCommandListener tracingCommandListener() {
    return new TracingCommandListener(tracer);
}
```
