package com.example.repository;

import com.example.domain.Transaction;
import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountNumber(String accountNumber);
    List<Transaction> findByAccountNumberAndCreatedAtBetween(String accountNumber, Instant start, Instant end);
    List<Transaction> findByType(Transaction.TransactionType type);
}
