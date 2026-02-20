package com.example.service;

import com.example.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final MongoTemplate mongoTemplate;

    public InventoryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public boolean reserveStock(String productId, int quantity) {
        Query query = new Query(Criteria.where("_id").is(productId)
            .and("stockQuantity").gte(quantity)
            .and("active").is(true));

        Update update = new Update()
            .inc("stockQuantity", -quantity)
            .set("updatedAt", java.time.Instant.now());

        Product product = mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            Product.class
        );

        if (product != null) {
            log.info("Reserved {} units of product {}", quantity, productId);
            return true;
        }

        log.warn("Failed to reserve stock for product {}", productId);
        return false;
    }

    public void releaseStock(String productId, int quantity) {
        Query query = new Query(Criteria.where("_id").is(productId));

        Update update = new Update()
            .inc("stockQuantity", quantity)
            .set("updatedAt", java.time.Instant.now());

        mongoTemplate.updateFirst(query, update, Product.class);
        log.info("Released {} units of product {}", quantity, productId);
    }

    public void addStock(String productId, int quantity) {
        Query query = new Query(Criteria.where("_id").is(productId));

        Update update = new Update()
            .inc("stockQuantity", quantity)
            .set("updatedAt", java.time.Instant.now());

        mongoTemplate.updateFirst(query, update, Product.class);
        log.info("Added {} units to product {}", quantity, productId);
    }

    public Optional<Product> getProduct(String productId) {
        return Optional.ofNullable(mongoTemplate.findById(productId, Product.class));
    }

    public boolean checkAvailability(String productId, int quantity) {
        Product product = mongoTemplate.findById(productId, Product.class);
        return product != null && product.hasEnoughStock(quantity);
    }
}
