package com.example;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ProductCacheService {

    private static final String CACHE_PREFIX = "product:";
    private static final long CACHE_TTL_MINUTES = 30;

    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Product> redisTemplate;

    public ProductCacheService(MongoTemplate mongoTemplate, RedisTemplate<String, Product> redisTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.redisTemplate = redisTemplate;
    }

    public Product save(Product product) {
        Product saved = mongoTemplate.save(product);
        redisTemplate.opsForValue().set(CACHE_PREFIX + saved.getId(), saved, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return saved;
    }

    public Optional<Product> findById(String id) {
        String cacheKey = CACHE_PREFIX + id;
        Product cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Optional.of(cached);
        }

        Product product = mongoTemplate.findById(id, Product.class);
        if (product != null) {
            redisTemplate.opsForValue().set(cacheKey, product, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        return Optional.ofNullable(product);
    }

    public void deleteById(String id) {
        Product product = mongoTemplate.findById(id, Product.class);
        if (product != null) {
            mongoTemplate.remove(product);
        }
        redisTemplate.delete(CACHE_PREFIX + id);
    }
}
