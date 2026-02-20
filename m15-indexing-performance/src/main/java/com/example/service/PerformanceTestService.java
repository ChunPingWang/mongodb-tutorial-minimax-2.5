package com.example.service;

import com.example.domain.AccountIndex;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class PerformanceTestService {

    private final MongoTemplate mongoTemplate;
    private final Random random = new Random();

    public PerformanceTestService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void seedData(int count) {
        List<String> branchIds = List.of("BR001", "BR002", "BR003", "BR004", "BR005");
        List<String> accountTypes = List.of("SAVINGS", "CHECKING", "INVESTMENT");
        List<String> statuses = List.of("ACTIVE", "INACTIVE", "SUSPENDED");

        List<AccountIndex> accounts = IntStream.range(0, count)
            .mapToObj(i -> {
                AccountIndex account = new AccountIndex(
                    String.format("ACC%08d", i),
                    String.format("CUST%05d", random.nextInt(10000)),
                    branchIds.get(random.nextInt(branchIds.size())),
                    accountTypes.get(random.nextInt(accountTypes.size())),
                    BigDecimal.valueOf(random.nextDouble() * 100000)
                );
                account.setStatus(statuses.get(random.nextInt(statuses.size())));
                account.setEmail("user" + i + "@example.com");
                return account;
            })
            .toList();

        mongoTemplate.insertAll(accounts);
    }

    public long testQueryWithoutIndex() {
        long start = System.nanoTime();
        Query query = new Query(Criteria.where("email").is("user5000@example.com"));
        mongoTemplate.find(query, AccountIndex.class);
        return System.nanoTime() - start;
    }

    public long testQueryWithIndex() {
        long start = System.nanoTime();
        Query query = new Query(Criteria.where("accountNumber").is("ACC00005000"));
        mongoTemplate.find(query, AccountIndex.class);
        return System.nanoTime() - start;
    }

    public long testCompoundIndexQuery() {
        long start = System.nanoTime();
        Query query = new Query(
            Criteria.where("branchId").is("BR001")
                .and("status").is("ACTIVE")
        );
        mongoTemplate.find(query, AccountIndex.class);
        return System.nanoTime() - start;
    }

    public long testRangeQuery() {
        long start = System.nanoTime();
        Query query = new Query(
            Criteria.where("accountType").is("SAVINGS")
                .and("balance").gte(BigDecimal.valueOf(50000))
        );
        mongoTemplate.find(query, AccountIndex.class);
        return System.nanoTime() - start;
    }

    public void createIndexes() {
        mongoTemplate.indexOps(AccountIndex.class)
            .ensureIndex(new org.springframework.data.mongodb.core.index.Index()
                .on("accountNumber", org.springframework.data.domain.Sort.Direction.ASC)
                .unique());
        mongoTemplate.indexOps(AccountIndex.class)
            .ensureIndex(new org.springframework.data.mongodb.core.index.Index()
                .on("email", org.springframework.data.domain.Sort.Direction.ASC));
    }

    public void dropIndexes() {
        mongoTemplate.indexOps(AccountIndex.class).dropAllIndexes();
    }

    public long countDocuments() {
        return mongoTemplate.count(new Query(), AccountIndex.class);
    }

    public void clearCollection() {
        mongoTemplate.remove(new Query(), AccountIndex.class);
    }
}
