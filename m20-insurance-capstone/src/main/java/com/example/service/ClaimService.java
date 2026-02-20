package com.example.service;

import com.example.domain.AutoClaim;
import com.example.domain.Claim;
import com.example.domain.HealthClaim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimService.class);

    private final MongoTemplate mongoTemplate;

    public ClaimService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public AutoClaim fileAutoClaim(String claimantId, String description, 
                                  String vehicleId, String accidentLocation) {
        AutoClaim claim = new AutoClaim(
            "AUTO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            claimantId,
            null,
            description,
            vehicleId,
            accidentLocation
        );
        
        AutoClaim saved = mongoTemplate.save(claim);
        log.info("Filed auto claim: {}", saved.getClaimNumber());
        return saved;
    }

    public HealthClaim fileHealthClaim(String claimantId, String description,
                                      String providerId, String procedureCode) {
        HealthClaim claim = new HealthClaim(
            "HEALTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            claimantId,
            null,
            description,
            providerId,
            procedureCode
        );
        
        HealthClaim saved = mongoTemplate.save(claim);
        log.info("Filed health claim: {}", saved.getClaimNumber());
        return saved;
    }

    public void approveClaim(String claimId) {
        Claim claim = mongoTemplate.findById(claimId, Claim.class);
        if (claim != null) {
            log.info("Approving claim: {}", claim.getClaimNumber());
        }
    }

    public void denyClaim(String claimId, String reason) {
        Claim claim = mongoTemplate.findById(claimId, Claim.class);
        if (claim != null) {
            log.info("Denying claim: {} for reason: {}", claim.getClaimNumber(), reason);
        }
    }

    public List<Claim> getClaimsByClaimant(String claimantId) {
        return mongoTemplate.find(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("claimantId").is(claimantId)
            ),
            Claim.class
        );
    }

    public void processClaim(Claim claim) {
        switch (claim) {
            case AutoClaim autoClaim -> processAutoClaim(autoClaim);
            case HealthClaim healthClaim -> processHealthClaim(healthClaim);
            case null -> log.warn("Claim is null");
        }
    }

    private void processAutoClaim(AutoClaim claim) {
        log.info("Processing auto claim: {}", claim.getClaimNumber());
        if (claim.getDamageSeverity() != null) {
            log.info("Damage severity: {}", claim.getDamageSeverity());
        }
    }

    private void processHealthClaim(HealthClaim claim) {
        log.info("Processing health claim: {}", claim.getClaimNumber());
        if (claim.isPreApproved()) {
            log.info("Claim is pre-approved");
        }
    }
}
