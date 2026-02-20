package com.example.service;

import com.example.domain.Transaction;
import com.example.domain.Transaction.TransactionType;
import com.example.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class TransactionQueryService {
    private final TransactionRepository transactionRepository;
    private final MongoTemplate mongoTemplate;

    public TransactionQueryService(TransactionRepository transactionRepository, MongoTemplate mongoTemplate) {
        this.transactionRepository = transactionRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Transaction> findByAccountNumber(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }

    public List<Transaction> findByDateRange(String accountNumber, LocalDate startDate, LocalDate endDate) {
        Instant start = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return transactionRepository.findByAccountNumberAndCreatedAtBetween(accountNumber, start, end);
    }

    public List<Transaction> findByType(TransactionType type) {
        return transactionRepository.findByType(type);
    }

    public List<Transaction> findByAmountGreaterThan(BigDecimal amount) {
        Query query = new Query(Criteria.where("amount").gt(amount));
        return mongoTemplate.find(query, Transaction.class);
    }

    public List<Transaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        Query query = new Query(Criteria.where("amount").gte(minAmount).lte(maxAmount));
        return mongoTemplate.find(query, Transaction.class);
    }

    public List<Transaction> findByAccountAndType(String accountNumber, TransactionType type) {
        Query query = new Query(Criteria.where("accountNumber").is(accountNumber).and("type").is(type));
        return mongoTemplate.find(query, Transaction.class);
    }

    public List<Transaction> findByAccountDateRangeAndType(String accountNumber, LocalDate startDate, LocalDate endDate, TransactionType type) {
        Instant start = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        Query query = new Query(
            Criteria.where("accountNumber").is(accountNumber)
                .and("createdAt").gte(start).lt(end)
                .and("type").is(type)
        );
        return mongoTemplate.find(query, Transaction.class);
    }

    public List<Transaction> findByMultipleTypes(List<TransactionType> types) {
        Query query = new Query(Criteria.where("type").in(types));
        return mongoTemplate.find(query, Transaction.class);
    }

    public List<Transaction> findByAccountWithAmountFilter(String accountNumber, BigDecimal minAmount) {
        Query query = new Query(
            Criteria.where("accountNumber").is(accountNumber)
                .and("amount").gte(minAmount)
        );
        return mongoTemplate.find(query, Transaction.class);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
