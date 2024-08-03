package com.investment_management.portfolio_service.repository;

import com.investment_management.portfolio_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepo extends JpaRepository<Transaction,String> {

    //will be used rarely
    Optional<List<Transaction>> findByPortfolioId(UUID portfolioId);

    Optional<Transaction> findById(UUID id);

    Optional<List<Transaction>> findByStatus(String status);
}
