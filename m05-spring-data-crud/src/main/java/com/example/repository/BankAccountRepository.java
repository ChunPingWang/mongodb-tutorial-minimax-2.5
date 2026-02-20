package com.example.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.domain.BankAccount;

public interface BankAccountRepository extends MongoRepository<BankAccount, String> {
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
