package com.example.port;

import com.example.domain.LoanApplication;
import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository {

    LoanApplication save(LoanApplication application);

    Optional<LoanApplication> findById(String id);

    List<LoanApplication> findAll();

    List<LoanApplication> findByStatus(LoanApplication.LoanStatus status);

    List<LoanApplication> findByApplicantEmail(String email);

    void deleteById(String id);

    boolean existsById(String id);
}
