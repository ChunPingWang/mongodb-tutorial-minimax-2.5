package com.example.adapter;

import com.example.domain.Applicant;
import com.example.domain.LoanApplication;
import com.example.port.LoanApplicationRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MongoLoanApplicationRepository implements LoanApplicationRepository {

    private final MongoTemplate mongoTemplate;

    public MongoLoanApplicationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public LoanApplication save(LoanApplication application) {
        return mongoTemplate.save(application, "loan_applications");
    }

    @Override
    public Optional<LoanApplication> findById(String id) {
        LoanApplication result = mongoTemplate.findById(id, LoanApplication.class, "loan_applications");
        return Optional.ofNullable(result);
    }

    @Override
    public List<LoanApplication> findAll() {
        return mongoTemplate.findAll(LoanApplication.class, "loan_applications");
    }

    @Override
    public List<LoanApplication> findByStatus(LoanApplication.LoanStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, LoanApplication.class, "loan_applications");
    }

    @Override
    public List<LoanApplication> findByApplicantEmail(String email) {
        Query query = new Query(Criteria.where("applicant.email").is(email));
        return mongoTemplate.find(query, LoanApplication.class, "loan_applications");
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, "loan_applications");
    }

    @Override
    public boolean existsById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.exists(query, LoanApplication.class, "loan_applications");
    }
}
