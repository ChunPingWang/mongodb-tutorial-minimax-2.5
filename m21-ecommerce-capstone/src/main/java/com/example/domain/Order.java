package com.example.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String orderNumber;
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private String shippingStatus;
    private Instant createdAt;
    private Instant updatedAt;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(String orderNumber, String customerId) {
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
        this.shippingStatus = "PENDING";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void addItem(String productId, String productName, int quantity, BigDecimal price) {
        OrderItem item = new OrderItem(productId, productName, quantity, price);
        this.items.add(item);
        calculateTotal();
        this.updatedAt = Instant.now();
    }

    private void calculateTotal() {
        this.totalAmount = items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirm() {
        this.status = "CONFIRMED";
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = "CANCELLED";
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal price;

        public OrderItem() {
        }

        public OrderItem(String productId, String productName, int quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
