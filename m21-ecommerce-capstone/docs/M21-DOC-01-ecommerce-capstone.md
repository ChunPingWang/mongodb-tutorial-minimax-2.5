# M21: 電商平台 Capstone

## 模組簡介

本模組建構電商核心系統，強調高併發與最終一致性。

## 專案架構

### Polyglot Persistence

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                             │
└─────────────────────────────────────────────────────────────┘
         │              │              │              │
         ▼              ▼              ▼              ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│   Product   │  │   Cart      │  │   Order     │  │   Inventory  │
│   Catalog   │  │   Service   │  │   Service   │  │   Service    │
│  (MongoDB)  │  │  (Redis)    │  │  (MongoDB)  │  │(MongoDB+Redis)│
└─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘
```

### 核心功能

#### 1. 產品目錄 (多型 + CQRS)

```java
// 多型商品
public sealed interface Product permits 
    PhysicalProduct, DigitalProduct, ServiceProduct {
    
    String sku();
    String name();
    BigDecimal getPrice();
}

// Read Model - 列表頁
@Document(collection = "product_list_views")
public class ProductListView {
    private String sku;
    private String name;
    private BigDecimal price;
    private String thumbnailUrl;
    private boolean inStock;
}

// Read Model - 搜尋引擎
@Document(collection = "product_search_views")
public class ProductSearchView {
    private String sku;
    private String name;
    private String description;
    private List<String> keywords;
    private Map<String, Object> facets;
}
```

#### 2. 購物車 (Redis + MongoDB)

```java
@Service
public class CartService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void addToCart(String customerId, String sku, int quantity) {
        // Redis: 即時購物車
        String cartKey = "cart:" + customerId;
        redisTemplate.opsForHash().increment(cartKey, sku, quantity);
        
        // 異步寫入 MongoDB 持久化
        eventPublisher.publishEvent(new CartUpdatedEvent(customerId));
    }
}
```

#### 3. 訂單 SAGA

```java
@Service
public class OrderSagaService {

    public void createOrderSaga(CreateOrderCommand command) {
        // Step 1: 庫存扣減 (可補償)
        try {
            inventoryService.reserveStock(command.getItems());
        } catch (Exception e) {
            throw new OrderFailedException("庫存不足");
        }
        
        // Step 2: 付款處理 (可補償)
        try {
            paymentService.processPayment(command.getCustomerId(), command.getTotal());
        } catch (Exception e) {
            inventoryService.releaseStock(command.getItems());
            throw new OrderFailedException("付款失敗");
        }
        
        // Step 3: 建立訂單 (最終)
        orderService.createOrder(command);
    }
}
```

### 高併發庫存扣減

```java
@Service
public class InventoryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean deductStock(String sku, int quantity) {
        // 原子操作: 庫存 >= 扣減數量才扣減
        Query query = Query.query(
            Criteria.where("sku").is(sku)
                .and("stock").gte(quantity)
        );
        
        Update update = new Update().inc("stock", -quantity);
        
        UpdateResult result = mongoTemplate.updateFirst(query, update, Inventory.class);
        
        return result.getModifiedCount() > 0;
    }
}
```

## 實作內容

- **Product Catalog**: 多型商品 + 多維搜尋
- **Shopping Cart**: Redis + MongoDB 混合
- **Order SAGA**: 庫存 → 付款 → 出貨
- **CQRS**: 商品列表 Read Model
- **Change Streams**: 庫存變動即時同步
- **壓力測試**: 模擬秒殺場景
