# M03: 開發環境與測試基礎設施

## 學習目標

建立標準化的 MongoDB 開發與測試環境，掌握 Testcontainers 和 BDD 測試流程。

## 1. Gradle 多模組專案建置

### 1.1 Root build.gradle.kts

```kotlin
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
```

### 1.2 settings.gradle.kts

```kotlin
rootProject.name = "mongodb-spring-course"
include("m01-rdb-vs-nosql")
include("m02-nosql-landscape")
// ... 其他模組
```

### 1.3 Version Catalog (libs.versions.toml)

```toml
[versions]
java = "23"
spring-boot = "3.4.0"

[libraries]
spring-boot-starter-data-mongodb = { group = "org.springframework.boot", name = "spring-boot-starter-data-mongodb" }

[plugins]
spring-boot = { id = "org.springframework.boot", version.ref = "spring-boot" }
```

## 2. Testcontainers + MongoDB 測試策略

### 2.1 基礎設定

```java
@Testcontainers
@SpringBootTest
abstract class MongoIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = 
        new MongoDBContainer("mongo:7.0")
            .withReuse(true);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", 
            mongoDBContainer::getReplicaSetUrl);
    }
}
```

### 2.2 Singleton Container Pattern

```java
@Container
static MongoDBContainer mongoDBContainer = 
    new MongoDBContainer("mongo:7.0")
        .withReuse(true);  // 重用容器加速測試
```

### 2.3 測試資料管理

```java
@SpringBootTest
class BankAccountRepositoryTest extends MongoIntegrationTest {

    @Autowired
    private BankAccountRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(BankAccount.class);
    }

    @Test
    void shouldSaveAccount() {
        BankAccount account = new BankAccount("A001", BigDecimal.valueOf(10000));
        repository.save(account);

        assertThat(repository.findByAccountNumber("A001"))
            .isPresent()
            .get()
            .extracting(BankAccount::getBalance)
            .isEqualByComparingTo(BigDecimal.valueOf(10000));
    }
}
```

## 3. BDD + TDD 雙軌測試流程

### 3.1 Cucumber 整合

#### Feature File

```gherkin
Feature: MongoDB 連線驗證
  Scenario: 成功連線至 MongoDB
    Given MongoDB container 已啟動
    When 執行 ping 命令
    Then 回傳成功狀態
```

#### Step Definition

```java
@CucumberContextConfiguration
@SpringBootTest
public class MongoStepDefs {

    private String pingResult;

    @Given("MongoDB container 已啟動")
    public void mongoDbContainer已啟動() {
        // Testcontainers 已自動啟動
    }

    @When("執行 ping 命令")
    public void 執行Ping命令() {
        // 執行 ping
    }

    @Then("回傳成功狀態")
    public void 回傳成功狀態() {
        assertThat(pingResult).isEqualTo("OK");
    }
}
```

### 3.2 測試金字塔

```
         ┌─────────────┐
         │  BDD (E2E)  │  ← End-to-End Scenarios
         ├─────────────┤
         │ Integration │  ← Service + Repository
         ├─────────────┤
         │    Unit     │  ← Pure Business Logic
         └─────────────┘
```

## 4. 專案骨架建立

### 4.1 標準 Module 結構

```
m03-environment-setup/
├── build.gradle.kts
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/
    │   │       ├── Application.java
    │   │       └── config/
    │   └── resources/
    │       └── application.yml
    └── test/
        ├── java/
        │   └── com/example/
        │       ├── MongoIntegrationTest.java
        │       └── steps/
        └── resources/
            └── features/
                └── mongodb.feature
```

### 4.2 Application 類

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 4.3 application.yml

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGO_URI:mongodb://localhost:27017/testdb}
      
server:
  port: 8080
```
