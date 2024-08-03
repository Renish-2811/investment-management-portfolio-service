package com.investment_management.portfolio_service.repository;

import com.investment_management.portfolio_service.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PortfolioRepo extends JpaRepository<Portfolio,String> {

    Optional<Portfolio> findByUserId(String userId);
}
