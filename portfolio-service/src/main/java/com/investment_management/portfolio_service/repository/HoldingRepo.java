package com.investment_management.portfolio_service.repository;

import com.investment_management.portfolio_service.entity.Holdings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoldingRepo extends JpaRepository<Holdings, UUID> {

    Optional<List<Holdings>> findByPortfolioId(UUID portfolioId);

    Optional<Holdings> findByStockCode(String stockCode);

    Optional<List<Holdings>> findByType(String type);
}
