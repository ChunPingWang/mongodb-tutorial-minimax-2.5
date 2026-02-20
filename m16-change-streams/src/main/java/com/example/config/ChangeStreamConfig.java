package com.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Configuration
public class ChangeStreamConfig {

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    private final MongoClient mongoClient;
    private final MongoTemplate mongoTemplate;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ChangeStreamConfig(MongoClient mongoClient, MongoTemplate mongoTemplate) {
        this.mongoClient = mongoClient;
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public Consumer<ChangeStreamDocument<Document>> changeStreamConsumer() {
        return changeStream -> {
            OperationType operationType = changeStream.getOperationType();
            System.out.println("Operation Type: " + operationType);

            if (changeStream.getFullDocument() != null) {
                System.out.println("Full Document: " + changeStream.getFullDocument().toJson());
            }

            if (changeStream.getNamespace() != null) {
                System.out.println("Namespace: " + changeStream.getNamespace());
            }
        };
    }

    @PostConstruct
    public void startChangeStreamListener() {
        executorService.submit(() -> {
            MongoCollection<Document> collection = mongoClient
                .getDatabase(databaseName)
                .getCollection("accounts");

            try (MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator()) {
                while (cursor.hasNext()) {
                    ChangeStreamDocument<Document> changeStream = cursor.next();
                    handleChangeStream(changeStream);
                }
            } catch (Exception e) {
                System.err.println("Change stream error: " + e.getMessage());
            }
        });
    }

    private void handleChangeStream(ChangeStreamDocument<Document> changeStream) {
        OperationType operationType = changeStream.getOperationType();
        
        switch (operationType) {
            case INSERT -> System.out.println("Document inserted: " + changeStream.getFullDocument());
            case UPDATE -> System.out.println("Document updated: " + changeStream.getFullDocument());
            case DELETE -> System.out.println("Document deleted: " + changeStream.getDocumentKey());
            case REPLACE -> System.out.println("Document replaced: " + changeStream.getFullDocument());
            case INVALIDATE -> System.out.println("Collection dropped or renamed");
            default -> System.out.println("Unknown operation: " + operationType);
        }
    }
}
