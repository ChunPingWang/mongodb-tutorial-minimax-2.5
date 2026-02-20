package com.example.listener;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.stereotype.Component;

@Component
public class AccountChangeListener extends AbstractMongoEventListener<Object> {

    private static final Logger log = LoggerFactory.getLogger(AccountChangeListener.class);

    @Override
    public void onAfterSave(AfterSaveEvent<Object> event) {
        log.info("Document saved: {} with id: {}", 
            event.getDocument().toJson(), 
            event.getDocument().getObjectId("_id"));
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Object> event) {
        log.info("Document deleted: {}", event.getDocument());
    }

    @Override
    public void onAfterConvert(AfterConvertEvent<Object> event) {
        log.debug("Document converted: {}", event.getDocument().toJson());
    }

    public void handleChangeStream(ChangeStreamDocument<Document> changeStream) {
        OperationType operationType = changeStream.getOperationType();
        
        switch (operationType) {
            case INSERT -> log.info("INSERT: New account created - {}", changeStream.getFullDocument());
            case UPDATE -> {
                log.info("UPDATE: Account updated - {}", changeStream.getFullDocument());
                Document fullDocument = changeStream.getFullDocument();
                if (fullDocument != null && fullDocument.containsKey("balance")) {
                    log.info("Balance change detected: {}", fullDocument.get("balance"));
                }
            }
            case DELETE -> log.info("DELETE: Account deleted - {}", changeStream.getDocumentKey());
            case REPLACE -> log.info("REPLACE: Account replaced - {}", changeStream.getFullDocument());
            default -> log.debug("Unhandled operation: {}", operationType);
        }
    }
}
