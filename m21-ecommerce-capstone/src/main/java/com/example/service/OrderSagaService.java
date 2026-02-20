package com.example.service;

import com.example.domain.Cart;
import com.example.domain.Order;
import com.example.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderSagaService {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaService.class);

    private final MongoTemplate mongoTemplate;
    private final InventoryService inventoryService;

    public OrderSagaService(MongoTemplate mongoTemplate, InventoryService inventoryService) {
        this.mongoTemplate = mongoTemplate;
        this.inventoryService = inventoryService;
    }

    public Order createOrderFromCart(String customerId) {
        String sagaId = UUID.randomUUID().toString();
        log.info("Starting order creation saga: {}", sagaId);

        try {
            Cart cart = mongoTemplate.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                    org.springframework.data.mongodb.core.query.Criteria.where("customerId").is(customerId)
                ),
                Cart.class
            );

            if (cart == null || cart.getItems().isEmpty()) {
                throw new IllegalStateException("Cart is empty");
            }

            Order order = new Order("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), customerId);

            for (Cart.CartItem cartItem : cart.getItems()) {
                boolean reserved = inventoryService.reserveStock(cartItem.getProductId(), cartItem.getQuantity());
                
                if (!reserved) {
                    throw new IllegalStateException("Insufficient stock for product: " + cartItem.getProductId());
                }

                order.addItem(
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getQuantity(),
                    java.math.BigDecimal.valueOf(cartItem.getPrice())
                );
            }

            order = mongoTemplate.save(order);

            mongoTemplate.remove(cart);

            log.info("Order created successfully: {}", order.getOrderNumber());
            return order;

        } catch (Exception e) {
            log.error("Order creation failed: {}", e.getMessage());
            compensateOrder(customerId);
            throw new RuntimeException("Order creation failed: " + e.getMessage(), e);
        }
    }

    private void compensateOrder(String customerId) {
        log.info("Compensating order for customer: {}", customerId);

        Cart cart = mongoTemplate.findOne(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("customerId").is(customerId)
            ),
            Cart.class
        );

        if (cart != null) {
            for (Cart.CartItem item : cart.getItems()) {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            }
        }
    }

    public void cancelOrder(String orderId) {
        Order order = mongoTemplate.findById(orderId, Order.class);
        
        if (order != null) {
            order.cancel();
            mongoTemplate.save(order);

            for (Order.OrderItem item : order.getItems()) {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            }

            log.info("Order cancelled and stock released: {}", order.getOrderNumber());
        }
    }
}
