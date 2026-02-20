package com.example.readmodel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderView {

    private String orderId;
    private String customerId;
    private String customerName;
    private List<OrderItemView> items;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private Instant createdAt;
    private Instant lastUpdatedAt;
    private int itemCount;

    public OrderView() {
    }

    public OrderView(String orderId, String customerId, String customerName,
                     List<OrderItemView> items, BigDecimal totalAmount, String status,
                     String shippingAddress, Instant createdAt, Instant lastUpdatedAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.itemCount = items != null ? items.stream().mapToInt(OrderItemView::quantity).sum() : 0;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<OrderItemView> getItems() {
        return items;
    }

    public void setItems(List<OrderItemView> items) {
        this.items = items;
        this.itemCount = items != null ? items.stream().mapToInt(OrderItemView::quantity).sum() : 0;
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

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public record OrderItemView(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {
    }
}
