package com.example.repository;

import com.example.domain.FinancialProduct;
import java.util.List;
import java.util.Optional;

public interface FinancialProductRepository {

    FinancialProduct save(FinancialProduct product);

    Optional<FinancialProduct> findById(String id);

    List<FinancialProduct> findAll();

    List<FinancialProduct> findByProductType(FinancialProduct.ProductType productType);

    List<FinancialProduct> findByProductNameContaining(String namePattern);

    void deleteById(String id);

    boolean existsById(String id);
}
