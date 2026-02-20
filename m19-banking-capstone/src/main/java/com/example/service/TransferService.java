package com.example.service;

import com.example.domain.Account;
import com.example.es.AccountEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountService accountService;
    private final AccountEventStore eventStore;

    public TransferService(AccountService accountService, AccountEventStore eventStore) {
        this.accountService = accountService;
        this.eventStore = eventStore;
    }

    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        String sagaId = UUID.randomUUID().toString();
        
        log.info("Starting transfer saga {} from {} to {} for amount {}", 
            sagaId, fromAccountId, toAccountId, amount);

        try {
            eventStore.saveSagaEvent(sagaId, "TRANSFER_INITIATED", 
                String.format("from=%s,to=%s,amount=%s", fromAccountId, toAccountId, amount));

            Account fromAccount = accountService.getAccount(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
            
            Account toAccount = accountService.getAccount(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

            fromAccount.debit(amount);
            eventStore.saveEvent(fromAccountId, "DEBIT_EVENT", fromAccount);
            
            toAccount.credit(amount);
            eventStore.saveEvent(toAccountId, "CREDIT_EVENT", toAccount);

            eventStore.saveSagaEvent(sagaId, "TRANSFER_COMPLETED", 
                String.format("from=%s,to=%s,amount=%s", fromAccountId, toAccountId, amount));
            
            log.info("Transfer saga {} completed successfully", sagaId);
            
        } catch (Exception e) {
            log.error("Transfer saga {} failed: {}", sagaId, e.getMessage());
            eventStore.saveSagaEvent(sagaId, "TRANSFER_FAILED", e.getMessage());
            compensateTransfer(fromAccountId, toAccountId, amount, sagaId);
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        }
    }

    private void compensateTransfer(String fromAccountId, String toAccountId, BigDecimal amount, String sagaId) {
        log.info("Compensating transfer saga {}", sagaId);
        
        eventStore.getAccount(toAccountId).ifPresent(account -> {
            account.debit(amount);
            eventStore.saveEvent(toAccountId, "COMPENSATION_DEBIT", account);
        });
        
        eventStore.getAccount(fromAccountId).ifPresent(account -> {
            account.credit(amount);
            eventStore.saveEvent(fromAccountId, "COMPENSATION_CREDIT", account);
        });
        
        eventStore.saveSagaEvent(sagaId, "TRANSFER_COMPENSATED", "Compensation completed");
    }
}
