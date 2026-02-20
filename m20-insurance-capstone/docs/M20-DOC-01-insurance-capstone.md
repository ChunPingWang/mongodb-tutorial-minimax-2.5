# M20: 保險理賠系統 Capstone

## 模組簡介

本模組建構保險理賠處理系統，強調多型建模與流程管理。

## 專案架構

### 多險種支援

```java
// 使用多型建模
public sealed interface Claim permits 
    AutoClaim, HealthClaim, PropertyClaim {
    
    String claimNumber();
    ClaimStatus getStatus();
    BigDecimal calculateSettlement();
}

// 車險理賠
public record AutoClaim(
    String claimNumber,
    ClaimStatus status,
    String accidentDescription,
    String vehiclePlateNumber,
    BigDecimal estimatedDamage,
    List<String> damagePhotos
) implements Claim {
    @Override
    public BigDecimal calculateSettlement() {...}
}

// 健康險理賠
public record HealthClaim(
    String claimNumber,
    ClaimStatus status,
    String diagnosis,
    List<MedicalExpense> expenses,
    BigDecimal deductible
) implements Claim {
    @Override
    public BigDecimal calculateSettlement() {...}
}
```

### 理賠流程 State Machine

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ RECEIVED │───▶│ ASSESSING │───▶│ APPROVED │───▶│ PAID    │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
       │                              │
       ▼                              ▼
  ┌──────────┐                  ┌──────────┐
  │ REJECTED │                  │ CANCELLED│
  └──────────┘                  └──────────┘
```

### Event Sourcing 理赔生命周期

```java
// 理赔事件溯源
@Document(collection = "claim_events")
public class ClaimEvent {
    private String claimId;
    private String eventType;
    private Document eventData;
    private Instant timestamp;
    private String performedBy;
}
```

## 實作內容

- **多型 Claim Documents**: 車險、健康險、財產險
- **Event Sourcing**: 理赔完整生命週期記錄
- **Aggregation Pipeline**: 理赔統計報表
- **Schema Validation**: 合規性檢查
- **SAGA**: 理赔 → 核赔 → 給付流程
