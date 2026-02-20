package com.example.migration;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

@Component
public class DocumentMigrator {

    private static final Logger log = LoggerFactory.getLogger(DocumentMigrator.class);
    private final MongoTemplate mongoTemplate;

    public DocumentMigrator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void migrate(String collectionName, int fromVersion, int toVersion) {
        log.info("Migrating collection {} from version {} to {}", collectionName, fromVersion, toVersion);
        
        List<Document> documents = mongoTemplate.findAll(Document.class, collectionName);
        
        for (Document doc : documents) {
            int currentVersion = doc.getInteger("schemaVersion", 1);
            
            if (currentVersion < toVersion) {
                for (int v = currentVersion; v < toVersion; v++) {
                    migrateStep(doc, v);
                }
                doc.put("schemaVersion", toVersion);
                doc.put("lastMigratedAt", Instant.now().toString());
                
                mongoTemplate.save(doc, collectionName);
                log.debug("Migrated document {} to version {}", doc.getObjectId("_id"), toVersion);
            }
        }
        
        log.info("Migration completed for collection {}", collectionName);
    }

    private void migrateStep(Document doc, int version) {
        switch (version) {
            case 1 -> migrateV1toV2(doc);
            case 2 -> migrateV2toV3(doc);
            case 3 -> migrateV3toV4(doc);
            default -> log.warn("No migration path for version {}", version);
        }
    }

    private void migrateV1toV2(Document doc) {
        if (doc.containsKey("firstName") && doc.containsKey("lastName")) {
            String fullName = doc.getString("firstName") + " " + doc.getString("lastName");
            doc.put("fullName", fullName);
            doc.remove("firstName");
            doc.remove("lastName");
        }
        if (doc.containsKey("createdDate")) {
            doc.put("createdAt", doc.get("createdDate"));
            doc.remove("createdDate");
        }
    }

    private void migrateV2toV3(Document doc) {
        if (doc.containsKey("email")) {
            doc.put("emailLower", doc.getString("email").toLowerCase());
        }
        if (doc.containsKey("phoneNumber")) {
            String phone = doc.getString("phoneNumber").replaceAll("[^0-9]", "");
            doc.put("phoneNormalized", phone);
        }
    }

    private void migrateV3toV4(Document doc) {
        doc.put("migrated", true);
        doc.put("migrationTimestamp", Instant.now().toString());
    }

    public void migrateWithTransformer(String collectionName, Function<Document, Document> transformer) {
        List<Document> documents = mongoTemplate.findAll(Document.class, collectionName);
        
        for (Document doc : documents) {
            Document transformed = transformer.apply(doc);
            mongoTemplate.save(transformed, collectionName);
        }
    }

    public long countByVersion(String collectionName, int version) {
        return mongoTemplate.count(
            Query.query(Criteria.where("schemaVersion").is(version)),
            collectionName
        );
    }
}
