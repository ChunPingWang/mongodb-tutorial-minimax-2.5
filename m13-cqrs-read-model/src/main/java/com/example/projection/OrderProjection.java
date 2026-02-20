package com.example.projection;

import com.example.domain.Order;
import com.example.readmodel.OrderView;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderProjection {

    private final MongoTemplate mongoTemplate;

    public OrderProjection(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void handleOrderCreated(Order order) {
        OrderView view = mapToView(order);
        mongoTemplate.save(view, "order_views");
    }

    public void handleOrderUpdated(Order order) {
        Query query = new Query(Criteria.where("orderId").is(order.getId()));
        OrderView existingView = mongoTemplate.findOne(query, OrderView.class, "order_views");
        
        if (existingView != null) {
            OrderView updatedView = mapToView(order);
            updatedView.setOrderId(existingView.getOrderId());
            mongoTemplate.save(updatedView, "order_views");
        } else {
            handleOrderCreated(order);
        }
    }

    public void handleOrderDeleted(String orderId) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        mongoTemplate.remove(query, "order_views");
    }

    public Optional<OrderView> findByOrderId(String orderId) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        return Optional.ofNullable(mongoTemplate.findOne(query, OrderView.class, "order_views"));
    }

    public List<OrderView> findByCustomerId(String customerId) {
        Query query = new Query(Criteria.where("customerId").is(customerId));
        return mongoTemplate.find(query, OrderView.class, "order_views");
    }

    public List<OrderView> findByStatus(String status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, OrderView.class, "order_views");
    }

    public List<OrderView> findAll() {
        return mongoTemplate.findAll(OrderView.class, "order_views");
    }

    private OrderView mapToView(Order order) {
        List<OrderView.OrderItemView> itemViews = order.getItems().stream()
            .map(item -> new OrderView.OrderItemView(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.unitPrice(),
                item.subtotal()
            ))
            .toList();

        return new OrderView(
            order.getId(),
            order.getCustomerId(),
            order.getCustomerName(),
            itemViews,
            order.getTotalAmount(),
            order.getStatus().name(),
            order.getShippingAddress(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}
