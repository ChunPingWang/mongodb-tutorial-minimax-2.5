package com.example.config;

import com.example.migration.DocumentMigrator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "migration.enabled", havingValue = "true", matchIfMissing = true)
public class MigrationConfig {

    private static final Logger log = LoggerFactory.getLogger(MigrationConfig.class);

    @Value("${migration.auto-migrate:true}")
    private boolean autoMigrate;

    @Value("${migration.target-version:4}")
    private int targetVersion;

    private final DocumentMigrator documentMigrator;

    public MigrationConfig(DocumentMigrator documentMigrator) {
        this.documentMigrator = documentMigrator;
    }

    @PostConstruct
    public void runMigrations() {
        if (autoMigrate) {
            log.info("Starting automatic migrations to version {}", targetVersion);
            documentMigrator.migrate("customers", 1, targetVersion);
            documentMigrator.migrate("accounts", 1, targetVersion);
            log.info("Migration completed");
        } else {
            log.info("Auto-migration disabled");
        }
    }
}
