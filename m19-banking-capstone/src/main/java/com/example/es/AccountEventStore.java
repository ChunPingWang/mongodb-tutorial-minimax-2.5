package com.example.es;

import com.example.domain.Account;
import com.example.domain.Transaction;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountEventStore {

    private final MongoTemplate mongoTemplate;

    public AccountEventStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void saveEvent(String aggregateId, String eventType, Account account) {
        Document event = new Document()
            .append("aggregateId", aggregateId)
            .append("eventType", eventType)
            .append("timestamp", Instant.now().toString())
            .append("accountNumber", account.getAccountNumber())
            .append("balance", account.getBalance().toString())
            .append("customerId", account.getCustomerId());
        
        mongoTemplate.insert(event, "account_events");
    }

    public void saveTransaction(Transaction transaction) {
        mongoTemplate.insert(transaction, "transactions");
    }

    public void saveSagaEvent(String sagaId, String eventType, String payload) {
        Document event = new Document()
            .append("sagaId", sagaId)
            .append("eventType", eventType)
            .append("payload", payload)
            .append("timestamp", Instant.now().toString());
        
        mongoTemplate.insert(event, "saga_events");
    }

    public Optional<Account> getAccount(String accountId) {
        Query query = new Query(Criteria.where("_id").is(accountId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Account.class));
    }
}
