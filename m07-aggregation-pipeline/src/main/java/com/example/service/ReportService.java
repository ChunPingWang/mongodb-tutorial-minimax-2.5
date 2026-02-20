package com.example.service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final MongoTemplate mongoTemplate;

    public ReportService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Map> getMonthlyTransactionSummary(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = yearMonth.atDay(1).toString();
        String endDate = yearMonth.atEndOfMonth().plusDays(1).toString();

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("createdAt").gte(startDate).lt(endDate)),
            Aggregation.group("type")
                .count().as("count")
                .sum("amount").as("totalAmount"),
            Aggregation.project()
                .and("_id").as("type")
                .and("count").as("count")
                .and("totalAmount").as("totalAmount")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "transactions", Map.class);
        return results.getMappedResults();
    }

    public Map getAccountMonthlySummary(String accountNumber, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = yearMonth.atDay(1).toString();
        String endDate = yearMonth.atEndOfMonth().plusDays(1).toString();

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("accountNumber").is(accountNumber)
                .and("createdAt").gte(startDate).lt(endDate)),
            Aggregation.group("type")
                .count().as("count")
                .sum("amount").as("totalAmount"),
            Aggregation.group()
                .sum("count").as("totalTransactions")
                .sum("totalAmount").as("totalAmount")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "transactions", Map.class);
        return results.getUniqueMappedResult();
    }

    public List<Map> getTopAccountsByTransactionVolume(int year, int month, int limit) {
        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = yearMonth.atDay(1).toString();
        String endDate = yearMonth.atEndOfMonth().plusDays(1).toString();

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("createdAt").gte(startDate).lt(endDate)),
            Aggregation.group("accountNumber")
                .sum("amount").as("totalVolume")
                .count().as("transactionCount"),
            Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "totalVolume"),
            Aggregation.limit(limit),
            Aggregation.project()
                .and("_id").as("accountNumber")
                .and("totalVolume").as("totalVolume")
                .and("transactionCount").as("transactionCount")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "transactions", Map.class);
        return results.getMappedResults();
    }

    public Map getDailyTransactionBreakdown(String accountNumber, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = yearMonth.atDay(1).toString();
        String endDate = yearMonth.atEndOfMonth().plusDays(1).toString();

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("accountNumber").is(accountNumber)
                .and("createdAt").gte(startDate).lt(endDate)),
            Aggregation.project()
                .and("createdAt").dateAsWithTimezone("UTC").as("date")
                .and("type").as("type")
                .and("amount").as("amount"),
            Aggregation.group("_id.date", "_id.type")
                .sum("amount").as("totalAmount")
                .count().as("count"),
            Aggregation.project()
                .and("_id").as("dateType")
                .and("totalAmount").as("totalAmount")
                .and("count").as("count")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "transactions", Map.class);
        return Map.of("breakdown", results.getMappedResults());
    }
}
