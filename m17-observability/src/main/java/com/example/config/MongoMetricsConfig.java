package com.example.config;

import com.mongodb.client.MongoClient;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommand;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsConnection;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoMetricsConfig {

    @Bean
    @Primary
    public MongoClient mongoClient(MongoProperties properties, MeterRegistry meterRegistry) {
        MongoClient mongoClient = properties.createMongoClient(null);
        
        new MongoMetricsCommand(meterRegistry, mongoClient, "mongodb")
            .bind();
        
        new MongoMetricsConnection(meterRegistry, mongoClient, "mongodb")
            .bind();
        
        return mongoClient;
    }
}
