# M11: 多型與繼承建模

## 學習目標

運用 OOP 多型概念在 MongoDB 實現靈活的文件結構。

## MongoDB 多型文件策略

### Single Collection Polymorphism

使用 discriminator field `_class` (Spring Data MongoDB 預設):

```java
@Document(collection = "financial_products")
@Inheritance(strategy = InheritanceType.SINGLE_COLLECTION)
public abstract class FinancialProduct {
    @Id
    private ObjectId id;
    private String productNumber;
    private String name;
    private BigDecimal currentValue;
}

public class Deposit extends FinancialProduct {
    private BigDecimal interestRate;
    private Integer termMonths;
}

public class Fund extends FinancialProduct {
    private BigDecimal nav;
    private RiskLevel riskLevel;
}

public class Insurance extends FinancialProduct {
    private Integer paymentYears;
    private BigDecimal coverage;
}
```

### Java Sealed Interface + MongoDB

```java
// 使用 Sealed Interface 定義有限類型
public sealed interface FinancialProduct 
    permits Deposit, Fund, Insurance {
    
    String productNumber();
    String name();
    BigDecimal currentValue();
}

// 具體實現
public record Deposit(
    String productNumber,
    String name,
    BigDecimal currentValue,
    BigDecimal interestRate,
    Integer termMonths
) implements FinancialProduct {}

public record Fund(
    String productNumber,
    String name,
    BigDecimal currentValue,
    BigDecimal nav,
    RiskLevel riskLevel
) implements FinancialProduct {}
```

### 查詢多型文檔

```java
public interface FinancialProductRepository 
    extends MongoRepository<FinancialProduct, ObjectId> {
    
    // 查詢所有產品
    List<FinancialProduct> findAll();
    
    // 查詢特定類型
    @Query("{ '_class' : 'Deposit' }")
    List<Deposit> findAllDeposits();
    
    @Query("{ '_class' : 'Fund' }")
    List<Fund> findAllFunds();
}
```
