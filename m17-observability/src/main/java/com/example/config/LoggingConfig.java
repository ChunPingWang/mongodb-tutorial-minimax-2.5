package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

@Configuration
public class LoggingConfig {

    @Bean
    public MongoEventLogger mongoEventLogger() {
        return new MongoEventLogger();
    }

    public static class MongoEventLogger extends AbstractMongoEventListener<Object> {
        private static final Logger log = LoggerFactory.getLogger(MongoEventLogger.class);

        @Override
        public void onBeforeConvert(BeforeConvertEvent<Object> event) {
            log.debug("Before convert: {}", event.getSource().getClass().getSimpleName());
        }

        @Override
        public void onBeforeSave(BeforeSaveEvent<Object> event) {
            log.info("Saving document: {}", event.getDocument().toJson());
        }
    }
}
