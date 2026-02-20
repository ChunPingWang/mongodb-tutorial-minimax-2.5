package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    private String id;
    private String customerId;
    private String customerName;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private Instant createdAt;
    private Instant updatedAt;

    public Order(String customerId, String customerName, String shippingAddress) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void addItem(String productId, String productName, int quantity, BigDecimal unitPrice) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to a non-pending order");
        }
        
        OrderItem item = new OrderItem(productId, productName, quantity, unitPrice);
        this.items.add(item);
        this.recalculateTotal();
        this.updatedAt = Instant.now();
    }

    public void submit() {
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Cannot submit an empty order");
        }
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be submitted");
        }
        this.status = OrderStatus.SUBMITTED;
        this.updatedAt = Instant.now();
    }

    public void confirm() {
        if (this.status != OrderStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public void ship() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed orders can be shipped");
        }
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = Instant.now();
    }

    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be delivered");
        }
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
            .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public record OrderItem(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
    ) {
        public OrderItem {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Unit price must be positive");
            }
        }

        public BigDecimal subtotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public enum OrderStatus {
        PENDING,
        SUBMITTED,
        CONFIRMED,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}
